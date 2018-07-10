package br.senai.collabtrack.client.util;

import android.os.AsyncTask;

import com.android.volley.VolleyError;

import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 05/10/2017.
 */

public abstract class HTTPAsyncTask {

    public HTTPAsyncTask(String url, Object obj) {
        //new LocalAsyncTask().execute(new String[]{url, obj.toString()});
        HttpUtil.post(obj.toString(), url, new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                success();
            }

            @Override
            public void onError(VolleyError error) {
                error();
            }
        });
    }

    public abstract void success();

    public abstract void error();

    private class LocalAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Server.post(params[0], params[1]);
                success();
            } catch (Exception e) {
                Application.log(e.getMessage(), e);
                error();
            }
            return null;
        }
    }
}
