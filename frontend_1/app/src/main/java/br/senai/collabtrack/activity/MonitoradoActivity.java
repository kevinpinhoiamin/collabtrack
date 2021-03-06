package br.senai.collabtrack.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.senai.collabtrack.R;
import br.senai.collabtrack.fragment.MonitoradoFragment;

public class MonitoradoActivity extends BaseActivity {

    private MonitoradoFragment monitoradoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorado);

        setUpToolbar(getString(R.string.monitorado) + " (" + (!getIntent().hasExtra("statusEditar") ? getString(R.string.cadastro).toLowerCase() : getString(R.string.edicao).toLowerCase()) + ")");
        setUpHomeButton();

        if(savedInstanceState == null){
            monitoradoFragment = new MonitoradoFragment();
            monitoradoFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_monitorado, monitoradoFragment).commit();
        }
    }
}
