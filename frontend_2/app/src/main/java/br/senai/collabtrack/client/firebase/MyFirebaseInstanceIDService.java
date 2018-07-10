package br.senai.collabtrack.client.firebase;

/**
 * Created by ezs on 23/08/2017.
 */

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.senai.collabtrack.client.services.MonitoradoService;
import br.senai.collabtrack.client.Application;

/**
 * Created by Lucas Matheus Nunes on 16/08/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private MonitoradoService monitoradoService = new MonitoradoService(Application.getContext());

    @Override
    public void onCreate() {
        onTokenRefresh();
    }

    //Isso não esta sendo chamado automaticamente
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Application.log("Refreshed token: " + refreshedToken);
        saveInPreference(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void saveInPreference(String token){
        SharedPreferences.Editor preferences = getSharedPreferences("APP_INFORMATION", MODE_PRIVATE).edit();
        preferences.putString("token_app", token);
        preferences.commit();
    }

    private void sendRegistrationToServer(String token){
        Application.log("Token sendRegistrationToServer: " + token);
        monitoradoService.refreshToken(token);
    }
}
