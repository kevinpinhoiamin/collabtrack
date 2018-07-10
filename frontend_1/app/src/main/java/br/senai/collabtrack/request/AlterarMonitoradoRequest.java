package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.to.MonitoradoTO;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by kevin on 10/20/17.
 */

public class AlterarMonitoradoRequest extends AsyncTask<Void, Void, Void>{

    private MonitoradoTO monitoradoTO;
    private HttpCallback httpCallback;

    public AlterarMonitoradoRequest(MonitoradoTO monitoradoTO, HttpCallback httpCallback){
        this.monitoradoTO = monitoradoTO;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.put(JsonUtil.toJson(monitoradoTO), CollabtrackApplication.API_PATH_MONITORADO, this.httpCallback);
        return null;
    }
}
