package br.senai.collabtrack.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import br.senai.collabtrack.client.R;
import br.senai.collabtrack.client.Application;

public class ScrollingActivity extends AppCompatActivity {

    private static TextView txtView = null;
    public static boolean activated = false;

    @Override
    protected void onResume() {
        super.onResume();
        activated = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activated = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activated = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtView = findViewById(R.id.text1);

        activated = true;
    }

    public static void setText(String text){
        if (activated)
            txtView.setText(text);
        else
            Application.getInstance().startActivity(new Intent(Application.getContext(), ScrollingActivity.class));
    }
}
