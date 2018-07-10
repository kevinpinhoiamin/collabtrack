package br.senai.collabtrack.client.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.senai.collabtrack.client.Application;

/**
 * Created by ezs on 09/08/2017.
 */

/*Service com thread*/
/*Implemente esta classe para obter um service rodando em cima de uma thread específica*/
public abstract class ServiceThread extends Service implements Runnable {

    private Thread thread;
    protected boolean running = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public abstract void code();

    @Override
    public void run() {
        Application.log(this.getClass().getSimpleName() + " STARTED");
        code();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        (thread = new Thread(this, this.getClass().getSimpleName())).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Application.log(this.getClass().getSimpleName() + " STOPED");
    }
}
