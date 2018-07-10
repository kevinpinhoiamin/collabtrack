package br.senai.collabtrack.client.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import br.senai.collabtrack.client.broadcast.BatteryStatusReceiver;
import br.senai.collabtrack.client.services.LocationService;
import br.senai.collabtrack.client.services.MensagemService;
import br.senai.collabtrack.client.services.MonitoradoService;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.R;

import static android.content.Intent.ACTION_CALL;

public class MainActivity extends AppCompatActivity {

    private static ImageButton imgBtnMonitor;
    private ImageButton imgBtnPerigo;
    private ImageButton imgBtnSeguro;

    public static boolean viewLoginCalled = false;
    private Activity thisActivity = null;

    @Override
    protected void onResume() {
        Application.log(this.getClass().getSimpleName() + " onResume called");
        if (!viewLoginCalled || (viewLoginCalled && SignInActivity.loginOk)) {
            Application.log(this.getClass().getSimpleName() + " -> " + "onResume");
            super.onResume();
        }    else {
            Application.log(this.getClass().getSimpleName() + " -> " + "onDestroy");
            System.exit(1);
        }
    }

    public static void setMonitorPhoto(){
        new MonitoradoService().loadPhotoMonitorado(MainActivity.imgBtnMonitor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        thisActivity = this;

        imgBtnMonitor = findViewById(R.id.imageButtonMonitor);
        imgBtnPerigo  = findViewById(R.id.imageButtonPerigo);
        imgBtnSeguro  = findViewById(R.id.imageButtonSeguro);

        final MonitoradoService monitoradoService = new MonitoradoService();

        if (monitoradoService.getThisMonitorado() == null) {
            showLogin();
        } else {
            Application.inicializaServicos();
            MainActivity.viewLoginCalled = false;
            Application.log("Monitorado já configurado no app: " + monitoradoService.getThisMonitorado().toString());
            monitoradoService.loadPhotoMonitorado(imgBtnMonitor);
            if (monitoradoService.emMonitoramento()){
                Application.log("Monitoramento ativo");
                startService(new Intent(getBaseContext(), BatteryStatusReceiver.class));
                startService(new Intent(getBaseContext(), LocationService.class));
            } else {
                Application.log("Monitoramento desligado");
            }
        }

        imgBtnMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long numeroCelMonitor = monitoradoService.getThisMonitorado().getCelularMonitor();

                Application.log("Call " + numeroCelMonitor);
                if (ContextCompat.checkSelfPermission(Application.getContext(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.CALL_PHONE},1);
                }

                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    startActivity(new Intent(ACTION_CALL, Uri.parse("tel:" + numeroCelMonitor)));
                else
                    Application.log("Não obteve permissão para iniciar a chamada");
            }
        });

        imgBtnSeguro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MensagemService().normalidade();
            }
        });

        imgBtnPerigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MensagemService().perigo();
            }
        });
    }

    public void showLogin() {
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
