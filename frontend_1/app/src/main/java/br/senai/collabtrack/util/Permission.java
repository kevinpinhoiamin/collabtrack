package br.senai.collabtrack.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kevin on 16/05/17.
 */

public class Permission {

    /**
     * Método responsável por pedir as permissões de localização do usuário
     *
     * @param activityCompat Activity na qual está requisitando a permissão
     */
    public static void requestLocationPermission(AppCompatActivity activityCompat, int requestCode){
        ActivityCompat.requestPermissions(activityCompat, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }

    /**
     * Método responsável por verificar se o usuário de permissão de acesso a localização
     *
     * @param fragmentActivity FragmentActivity na qual está verificando a permissão
     * @return true, caso tenha garantido as permissões, false caso contrário
     */
    public static boolean checkLocationPermission(FragmentActivity fragmentActivity){
        return ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(String[] permissions, int[] grantResults) {

        if (permissions.length == 1 &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }

    /**
     * Método responsável por verificar se a conexão com a internet está ativia
     * @param context Contexto em que o método está sendo chamado
     * @return true, caso tenha conexão, false caso contrário
     */
    public static boolean checkInternetConnection(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected()){
                return true;
            }
        }
        return false;
    }

}
