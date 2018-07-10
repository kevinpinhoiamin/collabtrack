package br.senai.collabtrack.client.firebase;

/**
 * Created by ezs on 23/08/2017.
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import br.senai.collabtrack.client.R;
import br.senai.collabtrack.client.activity.VoiceActivity;
import br.senai.collabtrack.client.broadcast.BatteryStatusReceiver;
import br.senai.collabtrack.client.object.DataFirebaseTo;
import br.senai.collabtrack.client.services.AudioService;
import br.senai.collabtrack.client.services.LocationService;
import br.senai.collabtrack.client.Application;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Application.log("my token " + refreshedToken);

        Application.log("firebase From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Application.log("firebase Message data payload: " + remoteMessage.getData());
            gerenciaMensagem(remoteMessage.getData().toString());
        }

        if (remoteMessage.getNotification() != null) {
            Application.log("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void handleNow() {
        Application.log("Short lived task is done.");
    }

    private void sendNotification(){
        sendNotification("atchin");
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, VoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.border)
                .setContentTitle("Collabtrack")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

    private void gerenciaMensagem(String mensagem){
        Application.log("gerencia mensagem: " + mensagem);
        JSONObject dataFirebaseTOJSON = null;
        DataFirebaseTo dataFirebaseTo = new DataFirebaseTo();
        try {
            dataFirebaseTOJSON = new JSONObject(mensagem);
            dataFirebaseTo.setAcao(Integer.parseInt(dataFirebaseTOJSON.get("acao").toString()));
            if (dataFirebaseTOJSON.toString().contains("mensagem"))
                dataFirebaseTo.setMensagem(dataFirebaseTOJSON.get("mensagem").toString());
        } catch (JSONException e) {
            Application.log(e);
        }

        switch (dataFirebaseTo.getAcao()) {
            case 0:
                Application.log("FIREBASE -> CHECK SERVICES STATUS SMARTPHONE");
                this.startService(BatteryStatusReceiver.class, dataFirebaseTo.getMensagem());
                this.startService(LocationService.class, dataFirebaseTo.getMensagem());
                break;
            case 5:
                Application.log("FIREBASE -> NEW AUDIO IN SERVER");
                new AudioService(getBaseContext()).checkPendingAudiosToAnswer();
                sendNotification("Audio");
                break;
            case 11:
                Application.log("FIREBASE -> MONITOR TROCOU A FOTO");
                break;
            default:
                Application.log("Ação não prevista: " + dataFirebaseTo.getAcao());
        }
    }

    private void startService(Class<?> classe, String command) {

        switch (command.toUpperCase()){
            case "START":
                if (!isMyServiceRunning(classe)) {
                    Application.log(classe.getSimpleName() + " Iniciando pois não estava em execução.");
                    startServiceAccordingToVersion(getBaseContext(),classe);
                } else
                    Application.log(classe.getSimpleName() + " Não iniciado pois ja estava em execução.");
                break;
            case "STOP":
                if (isMyServiceRunning(classe)) {
                    stopService(new Intent(getBaseContext(),classe));
                    Application.log(classe.getSimpleName() + " Parando pois estava em execução.");
                }else
                    Application.log(classe.getSimpleName() + " Não parado pois não estava em execução.");
                break;
            default:
                Application.log("Command não previsto recebido via FirebaseMessaging: " + command);
                break;
        }
    }

    public void startServiceAccordingToVersion(Context context, Class class0){
        Intent intent = new Intent(context, class0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            startService(intent);
        else
            startForegroundService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }
}
