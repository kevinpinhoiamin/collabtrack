package br.senai.collabtrack.client.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.senai.collabtrack.client.activity.VoiceActivity;
import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 02/10/2017.
 */

public class BroadcastReceiverActivity extends BroadcastReceiver {

    public BroadcastReceiverActivity() {
    }

    public void onReceive(Context context, Intent intent) {
        Application.getContext().startActivity(new Intent(Application.getContext(), VoiceActivity.class));
        /*Intent intentActivity = new Intent(context, VoiceActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentActivity);*/
    }
}
