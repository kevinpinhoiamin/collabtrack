package br.senai.collabtrack.request;

import android.os.AsyncTask;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.to.RespostaTO;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by lucas on 08/03/2018.
 */

public class CadastrarAvisoAreaSeguraRequest extends AsyncTask<Void, Void, Void> {
    private RespostaTO respostaTO;
    private HttpCallback httpCallback;
    public CadastrarAvisoAreaSeguraRequest(RespostaTO respostaTO, HttpCallback httpCallback){
        this.respostaTO= respostaTO;
        this.httpCallback= httpCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpUtil.post(JsonUtil.toJson(this.respostaTO), CollabtrackApplication.API_PATH_MENSAGEM_AREA_SEGURA, this.httpCallback);
        return null;
    }
}
