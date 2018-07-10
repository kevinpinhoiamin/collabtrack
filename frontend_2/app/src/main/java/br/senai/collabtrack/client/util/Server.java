package br.senai.collabtrack.client.util;

import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import br.senai.collabtrack.client.object.AppInfoException;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.services.StatusService;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by ezs on 17/08/2017.
 */

public class Server {

    public static synchronized String get(String url) throws AppInfoException {

        Application.log("get url: " + url);

        if (!StatusService.internetConectada())
            Application.log("Smartphone não conectado à internet!");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StringBuilder stringBuilder = new StringBuilder();

        try {
        com.loopj.android.http.HttpGet httpGet = new com.loopj.android.http.HttpGet(url);

        httpGet.setHeader(HttpUtil.getBasicAuthenticationParam(), HttpUtil.getBasicAuthenticationHash());

            HttpResponse response = HttpClientBuilder.create().build().execute(httpGet);


            HttpEntity entity = response.getEntity();
            if (entity != null) {

                BufferedReader result = new BufferedReader(new InputStreamReader(entity.getContent()));
                String temp = null;

                while ((temp = result.readLine()) != null) stringBuilder.append(temp);
                Application.log("content: " + stringBuilder.toString());
                return stringBuilder.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new AppInfoException("Falha ao acessar Web service -> " + e.getMessage(), e);
        }
    }

    public static String post(String url, JSONObject jsonObject) throws Exception {
        return post(url, jsonObject.toString());
    }

    public static String post(String url, Object object) throws Exception {
        return post(url, object.toString());
    }

    public synchronized static String post(String url, String contentJSON) throws Exception {

        Application.log("url: " + url);
        Application.log("content: " + contentJSON);
        if (StatusService.internetConectada()) {
            Application.log("internet ok");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);

            if (contentJSON != null && !contentJSON.isEmpty()) {
                StringEntity stringEntity = null;
                try {
                    stringEntity = new StringEntity(contentJSON);
                } catch (UnsupportedEncodingException e) {
                    throw new Exception(e.getMessage(), e);
                }
                httpPost.setEntity(stringEntity);
            }

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            ResponseHandler responseHandler = new BasicResponseHandler();
            try {
                Application.log("url: " + url);
                Application.log("content: " + contentJSON);
                httpClient.execute(httpPost, responseHandler);
                Application.log("success");
            } catch (IOException e) {
                Application.log("error: " + e.getCause() + " -> " + e.getMessage());
                throw new Exception(e.getMessage(), e);
            }
            return null;
        } else {
            Application.log("internet nok");
            throw new Exception("Smartphone não conectado à internet!");
        }
    }
}
