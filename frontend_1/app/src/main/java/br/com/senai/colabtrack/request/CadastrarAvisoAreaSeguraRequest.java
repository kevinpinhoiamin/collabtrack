package br.com.senai.colabtrack.request;

import android.os.AsyncTask;
import android.util.Log;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Mensagem;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;
import br.com.senai.colabtrack.domain.to.RespostaTO;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

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
        HttpUtil.post(JsonUtil.toJson(this.respostaTO), ColabTrackApplication.API_PATH_MENSAGEM_AREA_SEGURA, this.httpCallback);
        return null;
    }
}
