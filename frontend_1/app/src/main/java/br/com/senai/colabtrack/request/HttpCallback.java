package br.com.senai.colabtrack.request;

import com.android.volley.VolleyError;

import java.util.List;

import br.com.senai.colabtrack.domain.Monitor;

/**
 * Created by kevin on 9/26/17.
 */

public interface HttpCallback {
    void onSuccess(String response);
    void onError(VolleyError error);
}
