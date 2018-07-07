package br.com.senai.colabtrack.request;

import android.os.AsyncTask;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

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
        HttpUtil.put(JsonUtil.toJson(this.monitor), ColabTrackApplication.API_PATH_MONITOR, this.httpCallback);
        return null;
    }
}
