package br.com.senai.colabtrack.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.activity.MainActivity;

/**
 * Created by kevin on 11/13/17.
 */

public class NotificationUtil {

    public static void showNotification(String titulo, String message){

        Intent intent = new Intent(ColabTrackApplication.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ColabTrackApplication.getContext(), 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ColabTrackApplication.getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[] { 1000, 1000})
                .setColor(Color.parseColor("#83c3ed"))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) ColabTrackApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

}
