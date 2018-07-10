package br.senai.collabtrack.client.services;

import android.content.Context;

import com.android.volley.VolleyError;

import java.util.HashSet;
import java.util.Set;

import br.senai.collabtrack.client.object.MensagemTO;
import br.senai.collabtrack.client.sql.MensagemDAO;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.util.DataUtil;
import br.senai.collabtrack.client.util.HttpCallback;
import br.senai.collabtrack.client.util.HttpUtil;

/**
 * Created by ezs on 03/10/2017.
 */

public class MensagemService {

    private final String REST_MENSAGEM_AVISO = Application.SERVER_URL + "/mensagem/aviso";
    public static final boolean NORMALIDADE = true;
    public static final boolean PERIGO = false;
    public static Set<MensagemTO> pendingMessagesToAnswer = new HashSet<MensagemTO>();

    public MensagemDAO mensagemDAO = null;

    public MensagemService(){
        mensagemDAO = new MensagemDAO(Application.getContext());
    }

    public MensagemService(Context context){
         mensagemDAO = new MensagemDAO(context);
    }


    public void perigo() {
        enviarMensagem(MensagemService.PERIGO);
    }

    public void normalidade() {
        enviarMensagem(MensagemService.NORMALIDADE);
    }

    private void enviarMensagem(boolean resposta) {
        Application.log("enviarMensagem : -> id");
        Application.log("enviarMensagem : -> ");
        enviarMensagem(new MensagemTO(MonitoradoService.getThisMonitorado().getId(), MonitoradoService.getThisMonitorado().getIdMonitor(), DataUtil.getDateTime(), resposta));
    }

    public void checkPendingMessagesToAnswer() {

        for (MensagemTO mensagemTO : mensagemDAO.findAll()) {
            mensagemTO.setIdMonitorado(MonitoradoService.getThisMonitorado().getId());
            mensagemTO.setIdMonitor(MonitoradoService.getThisMonitorado().getIdMonitor());
            if (pendingMessagesToAnswer.add(mensagemTO)) {
                enviarMensagem(mensagemTO);
                Application.log("ADD OK -> " + mensagemTO.getId() + " | " + mensagemTO);
            } else
                Application.log("ADD NOK -> " + mensagemTO.getId() + " | " + mensagemTO);
        }
    }

    private void enviarMensagem(final MensagemTO mensagemTO) {

        HttpUtil.post(mensagemTO, REST_MENSAGEM_AVISO, false, new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                if (mensagemDAO.exists(mensagemTO.getId()))
                    mensagemDAO.delete(mensagemTO.getId());
                Application.log("ADD BEFORE REMOVE " + pendingMessagesToAnswer.size());
                pendingMessagesToAnswer.remove(mensagemTO);
                Application.log("ADD AFTER REMOVE " + pendingMessagesToAnswer.size());
            }

            @Override
            public void onError(VolleyError error) {
                if (!mensagemDAO.exists(mensagemTO.getId()))
                    mensagemDAO.save(mensagemTO);
                Application.log("ADD BEFORE REMOVE " + pendingMessagesToAnswer.size());
                pendingMessagesToAnswer.remove(mensagemTO);
                Application.log("ADD AFTER REMOVE " + pendingMessagesToAnswer.size());
            }
        });
    }
}
