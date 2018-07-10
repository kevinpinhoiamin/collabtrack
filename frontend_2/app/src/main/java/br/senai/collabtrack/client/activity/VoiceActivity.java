package br.senai.collabtrack.client.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import br.senai.collabtrack.client.R;
import br.senai.collabtrack.client.services.AppMediaPlayer;
import br.senai.collabtrack.client.services.AudioService;
import br.senai.collabtrack.client.Application;

public class VoiceActivity extends AppCompatActivity {

    private ImageButton imgBtnVoice;
    public static boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        VoiceActivity.started = true;

        Application.log(this.getClass().getSimpleName() + " onCreate called");

        imgBtnVoice = (ImageButton)findViewById(R.id.imageButtonVoiceMain);

        imgBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppMediaPlayer.getInstance(getBaseContext()).startNewAudio();
                } catch (Exception e){
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {

        Application.log(this.getClass().getSimpleName() + " onResume called");
        boolean status = new AudioService().nextAudioExistsInServer();
        Application.log("tem novo ? " + status);
        super.onResume();
        if (!status)
            finish();

    }

    @Override
    protected void onDestroy() {
        VoiceActivity.started = false;
        super.onDestroy();
    }
}
