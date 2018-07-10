package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.util.HttpUtil;

/**
 * Created by kevin on 11/20/17.
 */

public class BuscarMonitoradoRequest extends AsyncTask<Void, Void, Void>{

    private HttpCallback httpCallback;
    private long idMonitorado;

    public BuscarMonitoradoRequest(long idMonitorado, HttpCallback httpCallback){
        this.idMonitorado = idMonitorado;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpUtil.get(CollabtrackApplication.API_PATH_MONITORADO+"/monitores/"+idMonitorado, httpCallback);
        return null;
    }

}
