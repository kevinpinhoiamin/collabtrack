package br.senai.collabtrack.client.util;

/**
 * Created by ezs on 26/10/2017.
 */

import android.util.Base64;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.broadcast.NetworkStateReceiver;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Route;

/**
 * Created by kevin on 08/06/17.
 */

public class HttpUtil {

    private static final int BAD_REQUEST = 400;

    private static final int TIMEOUT_IN_MILLISECONDS = (60 * 1000);

    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    public static String getBasicAuthenticationParam(){
        return "Authorization";
    }

    public static String getBasicAuthenticationHash(){
        String credentials = Application.API_USER + ":" + Application.API_PASSWORD;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    public static Map<String, String> getBasicAuthenticationHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put(getBasicAuthenticationParam(), getBasicAuthenticationHash());
        return headers;
    }

    public static void post(final Object object, String url){
        post(object.toString(), url, null);
    }

    public static void post(final Object object, String url, boolean showError){
        post(object.toString(), url, showError, null);
    }

    public static void post(final Object object, String url, final HttpCallback callback){
        post(object.toString(), url, callback);
    }

    public static void post(final String json, String url, final HttpCallback httpCallback) {
        post(json, url, true, httpCallback);
    }

    public static void post(final Object object, String url, final boolean showError, final HttpCallback callback){
        post(object.toString(), url, showError, callback);
    }

    public static void post(final String json, String url, final boolean showError, final HttpCallback callback){

        NetworkStateReceiver.checkInternet();

        Application.log("URL.: " + url);
        Application.log("JSON: " + json);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Application.log("Status: Ok");
                if (callback!= null) callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Application.log("Status: Error");
                if (callback != null) callback.onError(error);
                if (showError)
                    defaultError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthenticationHeader();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return JSON_CONTENT_TYPE;
            }
        };

        CollabtrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void put(final Object object, String url, final HttpCallback callback){
        put(object.toString(), url, callback);
    }

    public static void put(final String json, String url, final HttpCallback callback){

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(callback != null){
                    callback.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                defaultError(error);
                if(callback != null){
                    callback.onError(error);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthenticationHeader();
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return  JSON_CONTENT_TYPE;
            }
        };

        CollabtrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void put(final Map<String, String> params, String url, final HttpCallback callback){

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultError(error);
                callback.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthenticationHeader();
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public String getBodyContentType() {
                return FORM_URL_ENCODED_CONTENT_TYPE;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_IN_MILLISECONDS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        CollabtrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void get(String url, final HttpCallback callback){

        Application.log("Disparando url: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
        Application.log("response: " + (response.isEmpty() ? "[vazio]" : response));
                callback.onSuccess(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
                defaultError(error);
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthenticationHeader();
            }
            @Override
            public String getBodyContentType() {
                return JSON_CONTENT_TYPE;
            }
        };

        CollabtrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void delete(final Object object, String url, final HttpCallback callback){
        delete(object.toString(), url, callback);
    }

    public static void delete(String url, final HttpCallback callback){

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                defaultError(error);
                callback.onError(error);
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthenticationHeader();
            }
        };

        CollabtrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    private static void defaultError(VolleyError error){

        String message = null;
        if (error.networkResponse instanceof NetworkResponse && error.networkResponse.data != null && error.networkResponse.statusCode == BAD_REQUEST) {
            try {
                message = new String(error.networkResponse.data,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else if (error instanceof NetworkError) {
            message = "Sem conexão com a internet... Por favor cheque sua conexão!";
        } else if (error instanceof ServerError) {
            message = "O servidor não foi encontrado. Por favor tente novamente depois de um tempo!!";
        } else if (error instanceof AuthFailureError) {
            message = "Falha de autenticação aplicativo mobile x servidor!";
        } else if (error instanceof ParseError) {
            message = "Erro de análise! Por favor tente novamente mais tarde.";
        } else if (error instanceof NoConnectionError) {
            message = "O servidor não está disponível! tente novamente mais tarde.";
        } else if (error instanceof TimeoutError) {
            message = "Tempo de requisição esgotado! Por favor cheque sua conexão.";
        }

        Application.log(error.getMessage());

        Application.toast(Application.getContext(), message);

        Application.log(String.valueOf(error));
        Application.log(message);

    }

    public static void getImage(String url, ImageButton view, Callback callback, boolean cache) {
        getImage(url, view, callback, cache, true);
    }

    public static void getImage(String url, ImageButton view, Callback callback, boolean cache, boolean showError) {
        Application.log(url);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().authenticator(new Authenticator()
                {
                    @Override
                    public okhttp3.Request authenticate(Route route, okhttp3.Response response) throws IOException
                    {
                        return response.request().newBuilder()
                                .header(getBasicAuthenticationParam(), getBasicAuthenticationHash())
                                .build();
                    }

                }).build();

        Picasso.Builder builder = new Picasso.Builder(Application.getContext()).downloader(new OkHttp3Downloader(okHttpClient));

        builder.build().setIndicatorsEnabled(true);
        builder.build().setLoggingEnabled(true);

        Application.log("Cache " + cache);

        if(cache) {
            Application.log("CACHE");
            builder.build().load(url).into(view, (Callback) callback);
        } else {
            Application.log("NO CACHE");
            builder.build().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(view, (Callback) callback);
        }
    }
}

