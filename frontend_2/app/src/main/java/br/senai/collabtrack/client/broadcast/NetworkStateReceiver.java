package br.senai.collabtrack.client.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import br.senai.collabtrack.client.services.AudioService;
import br.senai.collabtrack.client.services.MensagemService;
import br.senai.collabtrack.client.services.StatusService;
import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 10/08/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    private static ConnectivityManager cm;
    private static NetworkInfo activeNetwork;

    private static boolean internetOk = false;
    private static boolean isConnected = false;
    private static boolean isWifi = false;
    private static boolean isMobile = false;

    public static boolean isInternetOk(){
        boolean tempInternetVerify = internetOk || isConnected;
        Application.log("tempInternetVerify: " + tempInternetVerify);
        return tempInternetVerify;
    }

    public static void checkInternet(){
        if (!isInternetOk()){
            checkInternet(Application.getContext());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkInternet(context);
    }

    public void refreshConnectivityStatus(Context context) {
        checkInternet(context);
    }

    private static void checkInternet(Context context){
        internetOk = false;
        isConnected = false;
        isWifi = false;
        isMobile = false;

        Application.log("NetworkStateReceiver.refreshConnectivityStatus");

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        activeNetwork = cm.getActiveNetworkInfo();
        Application.log("NetworkStateReceiver.activeNetwork: " + (activeNetwork != null));

        if (activeNetwork != null) {

            isConnected = activeNetwork.isConnectedOrConnecting();
            Application.log("NetworkStateReceiver.isConnected: " + isConnected);

            if (isConnected) {

                isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;

                internetOk = (isWifi || isWifi);
            }
        }

        StatusService.getStatus().setWifi(isWifi);
        StatusService.getStatus().setInternetMovel(isMobile);

        if (internetOk) {

            StatusService.determinarAtualizacao(context);

            new MensagemService(context).checkPendingMessagesToAnswer();

            new AudioService(context).checkPendingAudiosToAnswer();
        }
    }
}