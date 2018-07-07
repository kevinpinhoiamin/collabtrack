package br.com.senai.colabtrack;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

/**
 * Created by Lucas Matheus Nunes on 16/08/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseId", "Refreshed token: " + refreshedToken);
        saveInPreference(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void saveInPreference(String token){
        SharedPreferences.Editor preferences = getSharedPreferences("APP_INFORMATION", MODE_PRIVATE).edit();
        preferences.putString("token_app", token);
        preferences.commit();
    }

    private void sendRegistrationToServer(String token){
        Log.d("SalvarToken", "Token: " + token);

        MonitorService monitorService = new MonitorService(getApplicationContext());
        Monitor monitor = monitorService.findAutenticado();

        if(monitor != null) {
            monitor.setToken(token);
            HttpUtil.put(JsonUtil.toJson(monitor), ColabTrackApplication.API_PATH_MONITOR, null);
        }
    }
}