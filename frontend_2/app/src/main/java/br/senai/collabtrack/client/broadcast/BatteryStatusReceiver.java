package br.senai.collabtrack.client.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.services.StatusService;

/**
 * Created by ezs on 22/06/2017.
 */

public class BatteryStatusReceiver extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Application.log(this.getClass().getSimpleName() + " STARTED");

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        getApplicationContext().registerReceiver(new BroadcastReceiver() {

            int levelLast = -1;

            @Override
            public void onReceive(Context context, Intent intent) {

                Application.log("BatteryStatusReceiver.onReceive");

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

                Application.log("level battery " + level);

                if (level != levelLast) {

                    levelLast = level;

                    StatusService.getStatus().setBateria(level);
                    StatusService.determinarAtualizacao();
                }
            }
        }, intentFilter);

        new NetworkStateReceiver().refreshConnectivityStatus(this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Application.log(this.getClass().getSimpleName() + " STOPED");
        StatusService.getStatus().setBateria(0);
        super.onDestroy();
    }
}
