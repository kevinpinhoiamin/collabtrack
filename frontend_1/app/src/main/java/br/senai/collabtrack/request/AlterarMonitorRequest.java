package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by kevin on 10/5/17.
 */

public class AlterarMonitorRequest extends AsyncTask<Void, Void, Void>{


    private Monitor monitor;
    private HttpCallback httpCallback;
    public AlterarMonitorRequest(Monitor monitor, HttpCallback httpCallback){
        this.monitor = monitor;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.put(JsonUtil.toJson(this.monitor), CollabtrackApplication.API_PATH_MONITOR, this.httpCallback);
        return null;
    }
}
