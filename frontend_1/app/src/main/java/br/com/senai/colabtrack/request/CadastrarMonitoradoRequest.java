package br.com.senai.colabtrack.request;

import android.os.AsyncTask;
import android.util.Log;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

/**
 * Created by kevin on 10/16/17.
 */

public class CadastrarMonitoradoRequest extends AsyncTask<Void, Void, Void>{

    private MonitoradoTO monitoradoTO;
    private HttpCallback httpCallback;
    public CadastrarMonitoradoRequest(MonitoradoTO monitoradoTO, HttpCallback httpCallback){
        this.monitoradoTO = monitoradoTO;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.post(JsonUtil.toJson(this.monitoradoTO), ColabTrackApplication.API_PATH_MONITORADO, this.httpCallback);
        return null;
    }
}
