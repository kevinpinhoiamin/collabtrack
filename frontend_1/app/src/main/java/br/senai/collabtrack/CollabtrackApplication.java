package br.senai.collabtrack;

import android.app.Application;
import android.content.Context;

/**
 * Created by kevin on 12/05/17.
 */

public class CollabtrackApplication extends Application{

    private static CollabtrackApplication instance;

    // Tags
    public static final String DEBUG_TAG = "CollabTrack";
    public static final String SQL_TAG = "SQL";

    // Base de dados
    public static final String SQL_ROW_ID_COLUMN = "rowid";
    public static final int SQL_ROW_ID_INDEX = 0;
    public static final String DATABASE = "collabtrack.sqlite";
    public static final Integer DATABASE_VERSION = 1;

    // Colorpicker
    public static final int COLOR_PICKER_DEFAULT_COLOR = -14654801;
    public static final int COLOR_PICKER_DEFAULT_BORDER_COLOR = -10965321;

    // API
    public static final boolean DEVELOPMENT_MODE = true;
    private static final String DOMAIN = DEVELOPMENT_MODE ? "http://b403b6e2.ngrok.io" : "http://54.207.111.104";
    private static final String API_PATH = DOMAIN + "/collabtrack/";
    public static final String API_USER = "collabtrack";
    public static final String API_PASSWORD = "NEiVyMFkeTCVHqHJIMyfGiAiRz4IvymaCnmdimi8";
    public static final String API_PATH_AREA_SEGURA = API_PATH+"area-segura";
    public static final String API_PATH_AREA_SEGURA_MONITORADO = API_PATH+"area-segura-monitorado";
    public static final String API_PATH_MONITOR = API_PATH+"monitor";
    public static final String API_PATH_MONITORADO = API_PATH+"monitorado";
    public static final String API_PATH_MONITOR_MONITORADO = API_PATH+"monitor-monitorado";
    public static final String API_PATH_MENSAGEM = API_PATH+"mensagem";
    public static final String API_PATH_MENSAGEM_AREA_SEGURA = API_PATH_MENSAGEM +"/areasegura";
    public static final String API_PATH_STATUS = API_PATH+"status";
    public static final String API_PATH_LOCALIZACAO = API_PATH+"localizacao";

    // UPLOADS
    private static final String UPLOAD_PATH = DOMAIN + "/uploads/";
    public static final String UPLOAD_MONITOR = UPLOAD_PATH + "monitor/";
    public static final String UPLOAD_MONITOR_EXTENSION = ".jpg";
    public static final String UPLOAD_AUDIO = UPLOAD_PATH + "audio/";

    // Broadcast Receiver
    public static final String LOCALIZACAO_BROADCAST_RECEIVER = "br.senai.collabtrack.fragment.GoogleMapFragment";
    public static final String STATUS_BROADCAST_RECEIVER = "br.senai.collabtrack.fragment.MonitoradoStatusFragment";
    public static final String MONITORADO_TO_BROADCAST_RECEIVER = "br.senai.collabtrack.domain.to.MonitoradoTO";

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
    }

    public static CollabtrackApplication getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }
}