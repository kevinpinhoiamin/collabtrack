package br.com.senai.colabtrack.request;

import android.os.AsyncTask;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.util.HttpUtil;

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
        HttpUtil.get(ColabTrackApplication.API_PATH_MONITORADO+"/monitores/"+idMonitorado, httpCallback);
        return null;
    }

}
