package br.senai.collabtrack.request;

import android.os.AsyncTask;

import com.android.volley.VolleyError;

import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.Mensagem;
import br.senai.collabtrack.service.MensagemService;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by lucas on 21/03/2018.
 */

public class SincronizarMensagemRequest extends AsyncTask<Void, Void, Void> {

    private long celular;

    public SincronizarMensagemRequest(long celular){
        this.celular = celular;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpUtil.get(CollabtrackApplication.API_PATH_MENSAGEM+"?celular="+celular,new HttpCallback() {
            @Override
            public void onSuccess (String response){

                //Serviços
                MensagemService mensagemService = new MensagemService(CollabtrackApplication.getContext());

                // Convertendo a lista de JSON para Object
                List<Mensagem> mensagens = JsonUtil.toObjectList(response, Mensagem[].class);

                // Iteração para efetuar os cadastros
                for (Mensagem mensagem : mensagens) {
                    mensagemService.save(mensagem);
                }

            }

            @Override
            public void onError (VolleyError error){}
        });

        return null;
    }
}
