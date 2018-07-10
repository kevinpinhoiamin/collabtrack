package br.senai.collabtrack.client.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import br.senai.collabtrack.client.R;
import br.senai.collabtrack.client.object.Audio;
import br.senai.collabtrack.client.services.AppMediaPlayer;
import br.senai.collabtrack.client.services.AudioService;
import br.senai.collabtrack.client.services.MensagemService;
import br.senai.collabtrack.client.Application;

public class VoiceActionActivity extends AppCompatActivity {

    private ImageButton imgBtnReplay;
    private ImageButton imgBtnPerigo;
    private ImageButton imgBtnNormalidade;

    public static boolean aboutToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_action);

        Application.log(this.getClass().getSimpleName() + " onCreate called");

        imgBtnReplay = (ImageButton)findViewById(R.id.imgBtnReplay);
        imgBtnPerigo = (ImageButton)findViewById(R.id.imgBtnNao);
        imgBtnNormalidade = (ImageButton)findViewById(R.id.imgBtnSim);

        imgBtnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppMediaPlayer.getInstance().replay();
            }
        });

        imgBtnNormalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AudioService().responder(new Audio(AppMediaPlayer.getInstance().currentAudioId, MensagemService.NORMALIDADE));
                finish();
            }
        });

        imgBtnPerigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AudioService().responder(new Audio(AppMediaPlayer.getInstance().currentAudioId, MensagemService.PERIGO));
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.log(this.getClass().getSimpleName() + " onResume called");
    }

    @Override
    protected void onDestroy() {
        Application.log(this.getClass().getSimpleName() + " onDestroy called");
        AppMediaPlayer.getInstance().stop();
        aboutToStart = false;
        super.onDestroy();
    }
}
