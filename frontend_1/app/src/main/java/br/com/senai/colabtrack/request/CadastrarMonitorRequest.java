package br.com.senai.colabtrack.request;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

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
        HttpUtil.post(JsonUtil.toJson(monitor), ColabTrackApplication.API_PATH_MONITOR, this.httpCallback);
        return null;
    }
}
