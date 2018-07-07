package br.com.senai.colabtrack.activity;

import android.os.Bundle;
import android.view.MenuItem;

import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.fragment.AreaSeguraFragment;

public class AreaSeguraActivity extends BaseActivity {

    private AreaSeguraFragment areaSeguraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_segura);

        setUpToolbar(getString(R.string.area_segura) + " (" + (!getIntent().hasExtra("areaSegura") ? getString(R.string.cadastro).toLowerCase() : getString(R.string.edicao).toLowerCase()) + ")");
        setUpHomeButton();

        if(savedInstanceState == null){
            areaSeguraFragment = new AreaSeguraFragment();
            areaSeguraFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_area_segura, areaSeguraFragment).commit();
        }

    }

}
