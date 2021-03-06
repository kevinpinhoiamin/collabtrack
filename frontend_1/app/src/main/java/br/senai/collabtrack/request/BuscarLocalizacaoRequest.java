package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.util.HttpUtil;

/**
 * Created by kevin on 3/8/18.
 */

public class BuscarLocalizacaoRequest extends AsyncTask<Void, Void, Void> {

    private HttpCallback httpCallback;
    private long[] monitorado;
    private int periodo;
    private int pontos;

    public BuscarLocalizacaoRequest(long[] monitorado, int periodo, int pontos, HttpCallback httpCallback) {
        this.monitorado = monitorado;
        this.periodo = periodo;
        this.pontos = pontos;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String queryString = "?";
        for(int i = 0; i < monitorado.length; i++) {
            queryString += (i > 0 ? "&" : "") + "monitorado=" + monitorado[i];
        }
        queryString += "&periodo=" + periodo;
        queryString += "&pontos=" + pontos;
        HttpUtil.get(CollabtrackApplication.API_PATH_LOCALIZACAO + queryString, this.httpCallback);
        return null;
    }

}
