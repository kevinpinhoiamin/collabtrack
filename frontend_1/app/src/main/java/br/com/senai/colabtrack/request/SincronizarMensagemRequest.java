package br.com.senai.colabtrack.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Mensagem;
import br.com.senai.colabtrack.service.MensagemService;
import br.com.senai.colabtrack.util.ConnectionUtil;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

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

        HttpUtil.get(ColabTrackApplication.API_PATH_MENSAGEM+"?celular="+celular,new HttpCallback() {
            @Override
            public void onSuccess (String response){

                //Serviços
                MensagemService mensagemService = new MensagemService(ColabTrackApplication.getContext());

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
