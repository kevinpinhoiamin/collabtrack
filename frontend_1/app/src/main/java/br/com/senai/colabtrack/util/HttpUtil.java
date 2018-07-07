package br.com.senai.colabtrack.util;


import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.ColabTrackSingleton;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.exception.ErrorResponseEntity;
import br.com.senai.colabtrack.request.HttpCallback;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Route;

/**
 * Created by kevin on 08/06/17.
 */

public class HttpUtil {

    private static final int BAD_REQUEST = 400;

    private static final int TIMEOUT_IN_MILLISECONDS = 60 * 100; // 60 segundos * 100 =  60000 millisegundos

    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    public static String getBasicAuthenticationParam(){
        return "Authorization";
    }

    public static String getBasicAuthenticationHash(){
        String credentials = ColabTrackApplication.API_USER+":"+ColabTrackApplication.API_PASSWORD;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    private static Map<String, String> getBasicAuthenticationHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put(getBasicAuthenticationParam(), getBasicAuthenticationHash());
        return headers;
    }
    private static RetryPolicy getRetryPolicy(){
        return new DefaultRetryPolicy(TIMEOUT_IN_MILLISECONDS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static void post(final String json, String url, final HttpCallback callback){

        if (!ConnectionUtil.isNetworkAvailable()) {
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
            public byte[] getBody() throws AuthFailureError {
                return json.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return JSON_CONTENT_TYPE;
            }
        };

        stringRequest.setRetryPolicy(getRetryPolicy());

        ColabTrackSingleton.getInstance().addToRequestQueue(stringRequest);

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
            public void onErrorResponse(VolleyError error) {
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

        stringRequest.setRetryPolicy(getRetryPolicy());

        ColabTrackSingleton.getInstance().addToRequestQueue(stringRequest);

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

        stringRequest.setRetryPolicy(getRetryPolicy());

        ColabTrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void get(String url, final HttpCallback callback){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
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

        stringRequest.setRetryPolicy(getRetryPolicy());

        ColabTrackSingleton.getInstance().addToRequestQueue(stringRequest);

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

        stringRequest.setRetryPolicy(getRetryPolicy());

        ColabTrackSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public static void getImage(String url, ImageView view, Callback callback, boolean cache){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator()
                {

                    @Override
                    public okhttp3.Request authenticate(Route route, okhttp3.Response response) throws IOException
                    {
                        //String credential = Credentials.basic(ColabTrackApplication.API_USER, ColabTrackApplication.API_PASSWORD);
                        return response.request().newBuilder()
                                .header(getBasicAuthenticationParam(), getBasicAuthenticationHash())
                                .build();
                    }

                })
                .build();

        Picasso.Builder builder = new Picasso.Builder(ColabTrackApplication.getContext())
                .downloader(new OkHttp3Downloader(okHttpClient));

        if(!cache) {
            builder
                .build()
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(view, callback);
        } else {
            builder
                .build()
                .load(url)
                .into(view, callback);
        }

    }

    private static void defaultError(VolleyError error){

        String message = null;
        if (error.networkResponse instanceof NetworkResponse && error.networkResponse.data != null && error.networkResponse.statusCode == BAD_REQUEST) {
            try {
                message = new String(error.networkResponse.data,"UTF-8");
                try {
                    ErrorResponseEntity errorResponse = (ErrorResponseEntity) JsonUtil.toObject(message, ErrorResponseEntity.class);
                    if(errorResponse instanceof  ErrorResponseEntity && errorResponse.getErrors().size() > 0) {
                        message = errorResponse.getErrors().get(0);
                    }
                } catch (Exception e) {}
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (error instanceof NetworkError) {
            message = ColabTrackApplication.getContext().getString(R.string.voce_nao_esta_conectado);
        } else if (error instanceof ServerError) {
            message = "Ocorreu um erro interno no servidor. Por favor tente novamente depois de um tempo!!";
        } else if (error instanceof AuthFailureError) {
            message = "Não foi possível conectar ao servidor... Por favor cheque sua conexão!";
        } else if (error instanceof ParseError) {
            message = "Erro de análise! Por favor tente novamente depois de um tempo!!";
        } else if (error instanceof NoConnectionError) {
            message = "Não foi possível conectar a internet... Por favor cheque sua conexão!";
        } else if (error instanceof TimeoutError) {
            message = "Tempo de requisição esgotado! Por favor cheque sua conexão.";
        }

        if(message != null){
            Toast.makeText(ColabTrackApplication.getContext(), message, Toast.LENGTH_LONG).show();
        }

        Log.e(ColabTrackApplication.DEBUG_TAG, String.valueOf(error));
        Log.e(ColabTrackApplication.DEBUG_TAG, message);

    }

}