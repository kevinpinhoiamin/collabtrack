package br.com.senai.colabtrack.request;

import android.os.AsyncTask;
import android.util.Log;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

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
        HttpUtil.put(JsonUtil.toJson(monitoradoTO), ColabTrackApplication.API_PATH_MONITORADO, this.httpCallback);
        return null;
    }
}
