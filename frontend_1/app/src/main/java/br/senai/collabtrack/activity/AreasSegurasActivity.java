package br.senai.collabtrack.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import br.senai.collabtrack.R;
import br.senai.collabtrack.fragment.AreasSegurasFragment;

public class AreasSegurasActivity extends BaseActivity {

    private AreasSegurasFragment areasSegurasFragment;
    private OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_seguras);

        setUpToolbar(R.string.areas_seguras);
        setUpHomeButton();

        if(savedInstanceState == null){
            areasSegurasFragment = new AreasSegurasFragment();
            areasSegurasFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout_areas_seguras, areasSegurasFragment).commit();
        }

        onBackPressedListener = areasSegurasFragment;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        onBackPressedListener.onBackPressed();
    }

    public interface OnBackPressedListener{
        void onBackPressed();
    }
}
