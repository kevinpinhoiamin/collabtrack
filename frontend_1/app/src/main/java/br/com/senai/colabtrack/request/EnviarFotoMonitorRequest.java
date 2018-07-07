package br.com.senai.colabtrack.request;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.IOUtil;

/**
 * Created by kevin on 10/2/17.
 */

public class EnviarFotoMonitorRequest extends AsyncTask<Void, Void, Void>{

    private Monitor monitor;
    private File foto;
    private HttpCallback httpCallback;
    public EnviarFotoMonitorRequest(Monitor monitor, File foto, HttpCallback httpCallback){
        this.monitor = monitor;
        this.foto = foto;
        this.httpCallback = httpCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            // Converte para Base64
            byte[] bytes = IOUtil.toBytes(new FileInputStream(foto));
            String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

            // Monta os dados da requisição
            Map<String, String> map = new HashMap<String, String>();
            map.put("id_monitor", String.valueOf(monitor.getId()));
            map.put("file_name", foto.getName());
            map.put("base64", base64);

            HttpUtil.put(map, ColabTrackApplication.API_PATH_MONITOR + "/picture", this.httpCallback);
        }catch (IOException e){
            Log.e(ColabTrackApplication.DEBUG_TAG, e.getMessage());
        }
        return null;
    }
}
