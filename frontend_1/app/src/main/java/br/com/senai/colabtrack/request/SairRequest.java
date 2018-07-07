package br.com.senai.colabtrack.request;

import android.os.AsyncTask;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

/**
 * Created by Lucas Matheus Nunes on 09/11/2017.
 */

public class SairRequest  extends AsyncTask<Void, Void, Void> {

    Monitor monitor;
    public SairRequest(Monitor monitor){
        this.monitor = monitor;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.put(JsonUtil.toJson(monitor), ColabTrackApplication.API_PATH_MONITOR+"/logout", null);
        return null;
    }
}
