package br.com.senai.colabtrack.activity;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.fragment.MonitoradoStatusFragment;
import br.com.senai.colabtrack.fragment.MonitoradosFragment;
import br.com.senai.colabtrack.fragment.GoogleMapFragment;
import br.com.senai.colabtrack.fragment.PerfilFragment;

/**
 * Created by kevin on 24/05/17.
 */

public class BaseActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    public final static int MENU_ITEM_MAPA = 0;
    public final static int MENU_ITEM_CHAT = 1;
    public final static int MENU_ITEM_MONITORADO = 2;
    public final static int MENU_ITEM_PERFIL = 3;

    // Fragment tags
    public final static String FRAGMENT_TAG_MAPA = "mapa";
    public final static String FRAGMENT_TAG_CHAT = "chat";
    public final static String FRAGMENT_TAG_MONITORADO = "monitorado";
    public final static String FRAGMENT_TAG_PERFIL = "perfil";
    private final static String[] fragmentTags = {FRAGMENT_TAG_MAPA, FRAGMENT_TAG_CHAT, FRAGMENT_TAG_MONITORADO, FRAGMENT_TAG_PERFIL};

    /**
     * Método responsável por criar a Toolbar
     *
     * @param title Título que será exibido na Toolbar
     */
    protected void setUpToolbar(int title){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    /**
     * Método responsável por criar a Toolbar
     *
     * @param title Título que será exibido na Toolbar
     */
    protected void setUpToolbar(CharSequence title){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    protected void setIdToolbar(int id){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setId(id);
            setSupportActionBar(toolbar);
        }
    }

    protected void setUpHomeButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Método responsável por configurar o BottomNavigation
     */
    protected void setUpBttomNavigation(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelected());
        disableShiftMode(bottomNavigationView);
    }

    /**
     * Método responsável por desabilitar o shift mode do BottomNavigation
     * https://stackoverflow.com/questions/41649494/how-to-remove-icon-animation-for-bottom-navigation-view-in-android
     * @param view
     */
    private static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    /**
     * Método responsável por trocar o item do menu que está selecionado e alterar o título na Toolbar
     * @param item Item que será selecionado
     */
    public void changeMenuItem(int item){
        if(bottomNavigationView != null){
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(item);
            if(menuItem != null){
                menuItem.setChecked(true);
                setUpToolbar(menuItem.getTitle());
            }
        }
    }

    /**
     * Método responsável por criar a instância do evento de clique em um item do BottomNavigation
     *
     * @return Instância do evento de clique
     */
    protected BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelected(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Se clicou no mesmo item, não recarrega o fragment
                if(getSupportActionBar().getTitle().equals(item.getTitle())){
                    return false;
                }

                // Fragment Manager e Fragment Transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();

                // Altera o título da Action Bar
                CharSequence title = item.getTitle();
                getSupportActionBar().setTitle(title);

                // Remove o fragments que estão ativos
                for (String fragmentTag : fragmentTags){
                    Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
                    if(fragment != null){
                        fragmentTransaction.remove(fragment);
                    }
                }

                // Faz o switch do item que foi clicado
                switch (item.getItemId()){
                    case R.id.nav_item_mapa: // Carrega o mapa
                        GoogleMapFragment googleMapFragment = new GoogleMapFragment();
                        fragmentTransaction.replace(R.id.mainFrame, googleMapFragment);
                        fragmentTransaction.addToBackStack(FRAGMENT_TAG_MAPA);
                        break;
                    case R.id.nav_item_chat:
                        MonitoradosFragment monitoradosFragment = new MonitoradosFragment();
                        fragmentTransaction.replace(R.id.mainFrame, monitoradosFragment);
                        fragmentTransaction.addToBackStack(FRAGMENT_TAG_CHAT);
                        break;
                    case R.id.nav_item_status:
                        MonitoradoStatusFragment monitoradoStatusFragment = new MonitoradoStatusFragment();
                        fragmentTransaction.replace(R.id.mainFrame, monitoradoStatusFragment);
                        fragmentTransaction.addToBackStack(FRAGMENT_TAG_MONITORADO);
                        break;
                    case R.id.nav_item_perfil: // Carrega o perfil
                        PerfilFragment perfilFragment = new PerfilFragment();
                        fragmentTransaction.replace(R.id.mainFrame, perfilFragment);
                        fragmentTransaction.addToBackStack(FRAGMENT_TAG_PERFIL);
                        break;
                }

                // Commita as transações do fragment
                fragmentTransaction.commit();

                return true;

            }
        };
    }

    /**
     * Método que verifica se o fragment está adicionado
     * @param fragmentTAG TAG do Fragment
     * @return TRUE caso esteja adicionado, FALSE caso contrário
     */
    public boolean isFragmentAdded(String fragmentTAG){
        return getFragmentManager().findFragmentByTag(fragmentTAG) != null;
    }

    /**
     * Método responsável por esconder o teclado
     */
    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String title = getSupportActionBar().getTitle().toString();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        MenuInflater menuInflater = getMenuInflater();

        if (title.equals(getString(R.string.mapa))){
            menuInflater.inflate(R.menu.top_navigation_menu_mapa, menu);
        }else if(title.equals(getString(R.string.monitorado))){
            menuInflater.inflate(R.menu.top_navigation_menu_monitorado_status, menu);
        }else if(title.equals(getString(R.string.areas_seguras))){
            menuInflater.inflate(R.menu.top_navigation_menu_areas_seguras, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.nav_item_pesquisar));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }else if(!title.equals(getString(R.string.perfil)) && !title.equals(getString(R.string.monitorado_cadastro)) && !title.equals(getString(R.string.chat)) && !title.equals(getString(R.string.monitorado_edicao))){
            menuInflater.inflate(R.menu.top_navigation_menu_chat, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
