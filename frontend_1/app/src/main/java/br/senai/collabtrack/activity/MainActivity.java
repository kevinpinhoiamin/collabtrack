package br.senai.collabtrack.activity;

import android.os.Bundle;

import br.senai.collabtrack.R;
import br.senai.collabtrack.fragment.GoogleMapFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState == null){
            // Configura a Toolbar
            super.setUpToolbar(R.string.mapa);

            if(!isFragmentAdded(FRAGMENT_TAG_MAPA)){
                changeMenuItem(MENU_ITEM_MAPA);
                GoogleMapFragment mapFragment = new GoogleMapFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, mapFragment, FRAGMENT_TAG_MAPA).commitAllowingStateLoss();
            }

        }else{
            super.setUpToolbar(savedInstanceState.getCharSequence("title"));
        }

        // Configura o BottomNavigation
        super.setUpBttomNavigation();

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("title", getSupportActionBar().getTitle());
    }

}
