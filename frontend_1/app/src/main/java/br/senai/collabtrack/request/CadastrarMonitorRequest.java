package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by kevin on 9/27/17.
 */

public class CadastrarMonitorRequest extends AsyncTask<Void, Void, Void>{

    Monitor monitor;
    private HttpCallback httpCallback;
    public CadastrarMonitorRequest(Monitor monitor, HttpCallback httpCallback){
        this.monitor = monitor;
        this.httpCallback = httpCallback;
    }




    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.post(JsonUtil.toJson(monitor), CollabtrackApplication.API_PATH_MONITOR, this.httpCallback);
        return null;
    }
}
