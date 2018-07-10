package br.senai.collabtrack.client.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.senai.collabtrack.client.activity.MainActivity;

/**
 * Created by ezequiel on 20/06/2017.
 */

public class AppAutoBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMainActivity);
    }
}
