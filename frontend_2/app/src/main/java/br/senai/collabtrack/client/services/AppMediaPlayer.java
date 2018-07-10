package br.senai.collabtrack.client.services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import br.senai.collabtrack.client.activity.VoiceActionActivity;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.util.HttpUtil;

/**
 * Created by ezs on 22/06/2017.
 */

public class AppMediaPlayer {

    public static MediaPlayer mediaPlayer = null;
    public static Long currentAudioId = 0L;

    static AudioService audioService = null;
    static AppMediaPlayer appMediaPlayer = null;

    public static AppMediaPlayer getInstance(){
        return appMediaPlayer != null ? appMediaPlayer : (appMediaPlayer = new AppMediaPlayer());
    }

    public static AppMediaPlayer getInstance(Context context){
        return appMediaPlayer != null ? appMediaPlayer : (appMediaPlayer = new AppMediaPlayer(context));
    }

    public AppMediaPlayer(){
        audioService = new AudioService(Application.getContext());
    }

    public AppMediaPlayer(Context context){
        audioService = new AudioService(context);
    }

    public static void startNewAudio() throws Exception {
        if (audioService.nextAudioExistsInServer()) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Application.log("DOWNLOAD AUDIO FROM " + audioService.getURL());

                mediaPlayer.setDataSource(Application.getContext(), Uri.parse(audioService.getURL()), HttpUtil.getBasicAuthenticationHeader());

                mediaPlayer.prepare();
            } catch (IOException e) {
                Application.log(e);
                throw new Exception("Falhao ao efetuar download do audio", e);
            }
            AppMediaPlayer.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!VoiceActionActivity.aboutToStart) {
                        VoiceActionActivity.aboutToStart = true;
                        //Intent intent = new Intent(Application.getContext(), VoiceActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //Application.getContext().startActivity(intent);
                        Application.getContext().startActivity(new Intent(Application.getContext(), VoiceActionActivity.class));
                    }
                }
            });
            mediaPlayer.start();
        } else {
            throw new Exception("Não tem áudio pra reproduzir");
        }
    }

    public static void replay() {
        mediaPlayer.start();
    }

    public static void stop() {
        mediaPlayer.stop();
    }
}
