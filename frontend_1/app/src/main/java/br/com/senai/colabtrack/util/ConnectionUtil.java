package br.com.senai.colabtrack.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import br.com.senai.colabtrack.ColabTrackApplication;

/**
 * Created by lucas on 22/03/2018.
 */

public class ConnectionUtil {

    public static  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ColabTrackApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isWifi(){
        ConnectivityManager connManager = (ConnectivityManager) ColabTrackApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }
}
