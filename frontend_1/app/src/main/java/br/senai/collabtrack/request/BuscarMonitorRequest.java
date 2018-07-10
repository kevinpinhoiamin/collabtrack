package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.util.HttpUtil;

/**
 * Created by kevin on 9/26/17.
 */

public class BuscarMonitorRequest extends AsyncTask<Void, Void, Void>{


    private static String URL;

    private long celular;
    private long idMonitor;
    private long idMonitorado;
    private String token;
    private HttpCallback httpCallback;

    public BuscarMonitorRequest(HttpCallback httpCallback){
        this.httpCallback = httpCallback;
    }

    public BuscarMonitorRequest(long idMonitorado, HttpCallback httpCallback){
        this.idMonitorado = idMonitorado;
        this.httpCallback = httpCallback;
    }

    public BuscarMonitorRequest(long celular, String token, HttpCallback httpCallback){
        this.celular = celular;
        this.token = token;
        this.httpCallback = httpCallback;
    }

    public BuscarMonitorRequest(long idMonitor, long idMonitorado, HttpCallback httpCallback){
        this.idMonitor = idMonitor;
        this.idMonitorado = idMonitorado;
        this.httpCallback = httpCallback;
        this.URL = pegaUrlMonitor();
    }

    @Override
    protected Void doInBackground(Void... params) {
        this.URL = pegaUrlMonitor();

        HttpUtil.get(this.URL, this.httpCallback);
        return null;
    }    

    public String pegaUrlMonitor(){
        String url = CollabtrackApplication.API_PATH_MONITOR;
        if(celular > 0 && token != null && !token.equals("")){
            url += "/login?celular="+celular+"&token="+token;
        }else if(celular > 0){
            url += "/celular/"+celular;
        }else if(idMonitor > 0 && idMonitorado > 0){
            url += "/monitores?id-monitor="+idMonitor+"&id-monitorado="+idMonitorado;
        }
        return url;
    }
}
