package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

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
        HttpUtil.put(JsonUtil.toJson(monitor), CollabtrackApplication.API_PATH_MONITOR+"/logout", null);
        return null;
    }
}
