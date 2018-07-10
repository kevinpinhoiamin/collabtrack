package br.senai.collabtrack.client.services;

import android.content.Context;
import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

import br.senai.collabtrack.client.activity.VoiceActivity;
import br.senai.collabtrack.client.object.Audio;
import br.senai.collabtrack.client.sql.AudioDAO;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.util.HTTPAsyncTask;
import br.senai.collabtrack.client.util.Server;

/**
 * Created by ezs on 04/10/2017.
 */

public class AudioService {

    private static final String REST_AUDIO_DOWNLOAD = Application.SERVER_URL + "/mensagem/audio";
    private static final String REST_AUDIO_NEXT_AUDIO_ID = Application.SERVER_URL + "/mensagem/proximoaudio/";
    private final String REST_MENSAGEM_RESPOSTA = Application.SERVER_URL + "/mensagem/resposta";

    private AudioDAO audioDAO = null;
    private MonitoradoService monitoradoService = null;
    private static Set<Audio> pendingAudiosToAnswer = new HashSet<Audio>();

    private Context context;

    public AudioService(){
        monitoradoService = new MonitoradoService();
        audioDAO = new AudioDAO();
    }
    public  AudioService(Context context){
        this.context = context;
        monitoradoService = new MonitoradoService(context);
        audioDAO = new AudioDAO(context);
    }

    public void checkPendingAudiosToAnswer() {

        //Verifica respostas não enviadas ao servidor
        for (Audio audio : audioDAO.findAll()) {
            Application.log("Encontrou áudio com resposta pendete a ser enviada para o servidor");
            Application.log(audio.toString());
            if (pendingAudiosToAnswer.add(audio)) {
                responder(audio);
                Application.log("ADD OK -> " + " | " + audio);
            } else {
                Application.log("ADD NOK -> " + " | " + audio);
            }
        }

        //Verifica novas pendências no servidor
        if (!VoiceActivity.started  && nextAudioExistsInServer())
            initializeActivityAudio();
    }

    private void initializeActivityAudio() {
        if (!VoiceActivity.started) {
            Intent intent = new Intent();
            intent.setAction("call.broadcastReceiverActivity");
            Application.getContext().sendBroadcast(intent);
            Application.getContext().startActivity(new Intent(Application.getContext(), VoiceActivity.class));
        }
    }

    public boolean nextAudioExistsInServer() {
        try {
            String url = REST_AUDIO_NEXT_AUDIO_ID + monitoradoService.getThisMonitorado(context).getId();
            Long nextId = Long.parseLong(Server.get(url));
            Application.log("nextAudioId = " + nextId);
            return (AppMediaPlayer.getInstance(context).currentAudioId = nextId) != 0L;
        } catch (Exception e) {
            Application.log(e);
            AppMediaPlayer.currentAudioId = 0L;
            return false;
        }
    }

    public String getURL() {
        return String.format("%s/%s/%s", REST_AUDIO_DOWNLOAD, monitoradoService.getThisMonitorado(context).getId(), AppMediaPlayer.currentAudioId);
    }

    public void responder(final Audio audio) {

        new HTTPAsyncTask(REST_MENSAGEM_RESPOSTA, audio) {
            @Override
            public void success() {
                audioDAO.delete(audio.getId());
                pendingAudiosToAnswer.remove(audio);
            }

            @Override
            public void error() {
                if (!audioDAO.exists(audio.getId()))
                    audioDAO.save(audio);
                pendingAudiosToAnswer.remove(audio);
            }
        };
    }
}
