package br.senai.collabtrack.client.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.object.Localizacao;
import br.senai.collabtrack.client.util.HttpUtil;

/**
 * Created by ezs on 03/08/2017.
 */

public class LocationService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Localizacao localizacao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Application.log(this.getClass().getSimpleName() + " STARTED");
        localizacao = new Localizacao(new MonitoradoService(getBaseContext()).getThisMonitorado(getBaseContext()));
        startSendLocatization();
        return START_STICKY;
    }

    public void startSendLocatization() {

        Application.log("Called method startSendLocatization");
        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                sendLocalization(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Application.log("-> -> onStatusChanged");
            }

            public void onProviderEnabled(String provider) {
                Application.log("-> -> onProviderEnabled");
                StatusService.getStatus().setLocalizacao(true);
                StatusService.atualizar();
            }

            public void onProviderDisabled(String provider) {
                Application.log("-> -> onProviderDisabled");
                StatusService.getStatus().setLocalizacao(false);
                StatusService.atualizar();
            }
        };

        Application.log("ACCESS_FINE_LOCATION " + ((ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? "ok" : "nok"));

        Application.log("ACCESS_COARSE_LOCATION " + ((ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? "ok" : "nok"));


        if (ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Application.log("Não obteve permissão para iniciar o requestLocationUpdates");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Application.LOCATION_CONFIG_MIN_TIME, Application.LOCATION_CONFIG_MIN_DISTANCE, locationListener);
    }

    public void sendLocalization(double latitude, double longitude){
        Application.log("send localization " + latitude + " " + longitude);
        localizacao.setLatitude(latitude);
        localizacao.setLongitude(longitude);
        enviar(localizacao);
    }

    public void enviar(Localizacao localizacao) {
        HttpUtil.post(localizacao, Application.SERVER_URL + "/localizacao", false);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

}
