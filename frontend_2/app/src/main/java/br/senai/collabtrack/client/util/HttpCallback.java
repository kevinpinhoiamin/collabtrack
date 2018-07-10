package br.senai.collabtrack.client.util;

import com.android.volley.VolleyError;

/**
 * Created by ezs on 26/10/2017.
 */

public interface HttpCallback {
    void onSuccess(String response);
    void onError(VolleyError error);
}
