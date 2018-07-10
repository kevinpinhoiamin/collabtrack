package br.senai.collabtrack.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.senai.collabtrack.client.activity.ScrollingActivity;
import br.senai.collabtrack.client.broadcast.BatteryStatusReceiver;
import br.senai.collabtrack.client.broadcast.NetworkStateReceiver;
import br.senai.collabtrack.client.firebase.MyFirebaseInstanceIDService;

/**
 * Created by ezs on 22/06/2017.
 */

public class Application extends android.app.Application{

    /******************************GLOBALS CONFIGURATIONS*******************************/

    public static final String SERVER_URL = "http://54.207.111.104/collabtrack";

    public static final String API_USER = "collabtrack";
    public static final String API_PASSWORD = "NEiVyMFkeTCVHqHJIMyfGiAiRz4IvymaCnmdimi8";

    public static final int LOCATION_CONFIG_MIN_TIME = 100;
    public static final int LOCATION_CONFIG_MIN_DISTANCE = 0;

    /*******************************END GLOBALS CONFIGURATIONS*******************************/

    private static final boolean showLogActivity = false;//Ativar para visualizar o log em campo; quando o app já estiver logado.

    public static String TAG = "log";

    private static StringBuilder stringBuilder = new StringBuilder();

    private static Application instance;

    @Override
    public void onCreate() {
        try {
            log("+++++++++++++++++++++ C L A S S  A P P  A P P L I C A T I O N onCreate +++++++++++++++++++++");
        } catch (Exception e){}
        this.instance = this;
        super.onCreate();
    }

    public static Application getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static void log(String text) {
        Log.e(TAG, text != null ? text : "Vazio");
        if (showLogActivity)
            logActivity(text);
    }

    public static void log(String text, Exception e) {
        Log.e(TAG, text, e);
        if (showLogActivity)
            logActivity(text + "\n" + convertExceptionToString(e));
    }

    public static void log(Exception e) {
        Log.e(TAG, "\n", e);
        if (showLogActivity)
            logActivity(convertExceptionToString(e));
    }

    public static void toast(Context context, String msg) {

        for (StackTraceElement el : Thread.currentThread().getStackTrace())
            Application.log("EZS -> Line: " + el.getLineNumber() + " Method: " + el.getMethodName() +" Class: " + el.getClassName());

        if (context == null) {
            log("toast nao sera exibido pq o context is null");
            return;
        }

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void inicializaServicos() {

        new NetworkStateReceiver().refreshConnectivityStatus(Application.getContext());

        Application.getContext().startService(new Intent(Application.getContext(), MyFirebaseInstanceIDService.class));

        Application.getContext().startService(new Intent(Application.getContext(), BatteryStatusReceiver.class));
    }

    public static void logActivity(String text)
    {
        stringBuilder.append(text + "\n");
        try {
            ScrollingActivity.setText(stringBuilder.toString());
        } catch (Exception e){
            Log.e(Application.TAG, "falha ao enviar dados para activity LOG");
        }
    }

    public static String convertExceptionToString(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
