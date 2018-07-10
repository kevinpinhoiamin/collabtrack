package br.senai.collabtrack.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.Chat;
import br.senai.collabtrack.domain.Mensagem;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.service.DataService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.sql.MensagemSQL;

public class ChatAdapter extends BaseAdapter{

    private Context ctx;
    private List<Chat> monitorados = null;
    private List<Monitor> monitores = null;
    private long idMonitor = 0;
    private long idMonitorado = 0;
    private DataService dataService = null;
    private MonitorService monitorService = null;
    private MensagemSQL mensagemSQL = null;

    public ChatAdapter(Context ctx, List<Chat> monitorados, long monitorId, long monitoradoId, List<Monitor> monitores){
        this.ctx = ctx;
        this.monitorados = monitorados;
        this.monitores = monitores;
        this.idMonitor = monitorId;
        this.idMonitorado = monitoradoId;
        this.mensagemSQL = new MensagemSQL(ctx);
        this.dataService = new DataService();
        this.monitorService = new MonitorService(ctx);
    }

    @Override
    public int getCount() {
        return monitorados.size();
    }

    @Override
    public Chat getItem(int position) {
        return monitorados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = null;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) ctx) .getLayoutInflater();
            v = inflater.inflate(R.layout.model_chats, null);
        }else{
            v = convertView;
        }

        Chat c = getItem(position);

        TextView txvMensagem = (TextView) v.findViewById(R.id.txvMensagem);
        TextView txvDataHora = (TextView) v.findViewById(R.id.txvDataHora);

        txvMensagem.setCompoundDrawables(null, null, null, null);
        txvMensagem.setPadding(15,0,35,19);

        LinearLayout containerChat = (LinearLayout)v.findViewById(R.id.containerChat);

        if(idMonitor == c.getIdUsuario()){
            txvMensagem.setBackgroundResource(R.drawable.bg_msg_send);
            containerChat.setGravity(Gravity.RIGHT);
        }else{
            txvMensagem.setBackgroundResource(R.drawable.bg_msg_received);
            containerChat.setGravity(Gravity.LEFT);
        }

            /* ------------------------ Quando for uma mensagem de Áudio -------------------------*/
        if(c.getTipo() == Chat.TIPO_AUDIO){
            Drawable img = CollabtrackApplication.getContext().getResources().getDrawable( R.drawable.ic_play_audio);
            img.setBounds( 0, 0, 70, 70 );
            txvMensagem.setCompoundDrawables(img, null, null, null);

            File file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "Collabtrack" + File.separator + idMonitorado+ File.separator + c.getId() +".mp3");

            if(file.exists()){
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(ctx,Uri.fromFile(file));
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                double seconds = millSecond / 1000.0;
                txvMensagem.setText("\n"+seconds+"s");
            }else{
                txvMensagem.setText("\nEnviando áudio...");
            }

            txvMensagem.setPadding(0,0,0,19);
            /* ------------------------ Quando for uma Resposta de Áudio -------------------------*/
        }else if(c.getTipo() == Chat.TIPO_RESPOSTA) {

            txvMensagem.setBackgroundResource(R.drawable.bg_msg_answer);
            containerChat.setGravity(Gravity.CENTER);

            Drawable img = CollabtrackApplication.getContext().getResources().getDrawable(R.drawable.ic_play_audio);
            //Esquerda, Cima, Direita, Baixo
            img.setBounds(0, 8, 60, 60);
            txvMensagem.setCompoundDrawables(img, null, null, null);

            File file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "Collabtrack" + File.separator + idMonitorado + File.separator + c.getIdResposta() + ".mp3");

            if (file.exists()) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(ctx, Uri.fromFile(file));
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);
                double seconds = millSecond / 1000.0;

                Mensagem mensagemAudio = mensagemSQL.find((long) c.getIdResposta());

                txvMensagem.setText(seconds + "s | " + dataService.converteDiaMesHora(mensagemAudio.getData()) + "\n" + c.getMensagem());
            }

            /* ------------------------ Quando for uma Mensagem de status -------------------------*/
        }else if(c.getTipo() == Chat.TIPO_STATUS || c.getTipo() == Chat.TIPO_DIVERSOS  || c.getTipo() == Chat.TIPO_AREA_SEGURA || c.getTipo() == Chat.TIPO_LOCALIZACAO){
            Drawable img = null;

            if(c.getTipo() == Chat.TIPO_AREA_SEGURA || (c.getTipo() == Chat.TIPO_STATUS && c.getMensagem().equals(ctx.getString(R.string.perigo)) )){
                if(c.getMensagem().equals(ctx.getString(R.string.perigo))){
                    img = CollabtrackApplication.getContext().getResources().getDrawable(R.drawable.ic_phone_chat);
                }else{
                    img = CollabtrackApplication.getContext().getResources().getDrawable(R.drawable.ic_localization);
                }
                txvMensagem.setBackgroundResource(R.drawable.bg_red);
            }else if(c.getTipo() == Chat.TIPO_STATUS ){
                img = CollabtrackApplication.getContext().getResources().getDrawable(R.drawable.ic_phone_chat);
                txvMensagem.setBackgroundResource(R.drawable.bg_green);
            }else{
                img = CollabtrackApplication.getContext().getResources().getDrawable(R.drawable.ic_phone_chat);
                txvMensagem.setBackgroundResource(R.drawable.bg_yellow);
            }
            containerChat.setGravity(Gravity.CENTER);
            //Esquerda, Cima, Direita, Baixo
            img.setBounds(0, 0, 60, 60);
            txvMensagem.setCompoundDrawables(img, null, null, null);
            txvMensagem.setText("\n"+c.getMensagem()+"\n");
            txvDataHora.setGravity(Gravity.RIGHT);

            /* ------------------------ Quando for uma Mensagem de texto normal -------------------------*/
        }else{
            Monitor monitorInfo = new Monitor();
            monitorInfo = monitorService.find((long) c.getIdUsuario());
            txvMensagem.setText(c.getMensagem()+"\n");

            if(idMonitor != c.getIdUsuario()) {
                if (monitores != null && monitores.size() > 0) {
                    for (Monitor monitor : monitores) {
                        if(monitor.getId() == c.getIdUsuario()){
                            txvMensagem.setText(monitor.getNome()+"\n\n"+c.getMensagem()+"\n");
                            break;
                        }
                    }
                }
            }
        }

        if(c.getData().length() == 0){
            txvDataHora.setText("\n\n Enviando ...");
        }else{
            boolean tempoPassado = dataService.dataMaiorQueUmDia(c.getData());
            if(tempoPassado){
                txvDataHora.setText(dataService.converteDiaMesHora(c.getData()));
            }else{
                txvDataHora.setText(dataService.retiraDataDeHora(c.getData()));
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat itemChat = getItem(position);
                if(itemChat.getTipo() == Chat.TIPO_AUDIO){

                    File file = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "Collabtrack" + File.separator + idMonitorado+ File.separator + itemChat.getId() +".mp3");

                    if(file.exists()){

                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(ctx, Uri.fromFile(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }else{
                        Toast.makeText(CollabtrackApplication.getContext(), "Áudio não encontrado. Este áudio foi excluído.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return v;
    }
}