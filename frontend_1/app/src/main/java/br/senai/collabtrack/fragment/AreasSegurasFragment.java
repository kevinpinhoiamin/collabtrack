package br.senai.collabtrack.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.activity.AreaSeguraActivity;
import br.senai.collabtrack.activity.AreasSegurasActivity;
import br.senai.collabtrack.adapter.AreaSeguraAdapter;
import br.senai.collabtrack.component.Loader;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.domain.AreaSeguraMonitorado;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.util.AreaSeguraMonitoradoUtil;
import br.senai.collabtrack.fragment.dialog.AreaSeguraDialogFragment;
import br.senai.collabtrack.fragment.dialog.AreaSeguraDialogExcluirFragment;
import br.senai.collabtrack.request.HttpCallback;
import br.senai.collabtrack.service.AreaSeguraMonitoradoService;
import br.senai.collabtrack.service.AreaSeguraService;
import br.senai.collabtrack.service.MonitorMonitoradoService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.domain.util.AreaSeguraUtil;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by kevin on 26/05/17.
 */

public class AreasSegurasFragment extends BaseFragment implements AreasSegurasActivity.OnBackPressedListener{

    // Fragment
    private AreasSegurasFragment thisFragment = this;

    // Recycler View e Adapter
    private RecyclerView recyclerView;
    private AreaSeguraAdapter areaSeguraAdapter;

    // Áreas seguras
    private List<AreaSegura> areaSeguraList;
    private List<AreaSegura> areaSegurasEscondidads;
    private List<AreaSegura> arreasSegurasRemovidas;
    private List<AreaSegura> areasSegurasEditadas;
    private AreaSegura areaSeguraMostrar;
    private String pesquisa = "";

    // Monitor autenticado
    private Monitor monitor;

    // Lista com as relações entre o monitor autenticado e os monitorados
    private List<MonitorMonitorado> monitorMonitorados;

    // Classe de serviço
    private AreaSeguraService areaSeguraService;

    // Request
    private static int DIALOG_AREA_SEGURA_REQUEST= 1;
    private static int EDIT_AREA_SEGURA_REQUEST = 2;
    private static int CONFIRMACAO_EXCLUIR_AREA_SEGURA_REQUEST = 3;

    // Search View
    private SearchView searchView;

    // Loader
    private Loader loader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Áreas seguras
        areaSegurasEscondidads = new ArrayList<>();
        areaSeguraService = new AreaSeguraService(getContext());
        arreasSegurasRemovidas = new ArrayList<>();
        areasSegurasEditadas = new ArrayList<>();
        if(savedInstanceState != null){
            areaSeguraList = AreaSeguraUtil.writeToAreaSeguraList(savedInstanceState.getParcelableArrayList("areasSeguras"));
            arreasSegurasRemovidas = AreaSeguraUtil.writeToAreaSeguraList(savedInstanceState.getParcelableArrayList("arreasSegurasRemovidas"));
            areasSegurasEditadas = AreaSeguraUtil.writeToAreaSeguraList(savedInstanceState.getParcelableArrayList("areasSegurasEditadas"));
            pesquisa = savedInstanceState.getString("pesquisa");
        }

        // Loader
        loader = new Loader(thisFragment.getContext());
        loader.setTitle(getString(R.string.aguarde));

        // Carrega os dados para obter as permissões inicias
        new CarregarDadosParaPermissoes().execute();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.nav_item_pesquisar);
        searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisa = newText;
                pesquisarAreasSeguras();
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_areas_seguras, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_areas_seguras);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        return view;
    }

    private AreaSeguraAdapter.AreaSeguraOnClickListener onClickAreaSegura(){
        return new AreaSeguraAdapter.AreaSeguraOnClickListener() {
            @Override
            public void onClickAreaSegura(View view, AreaSegura areaSegura) {
                new StartDialog(areaSegura).execute();
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (areaSeguraAdapter == null) {
            pesquisarAreasSeguras();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        areaSeguraList.addAll(areaSegurasEscondidads);
        savedInstanceState.putParcelableArrayList("areasSeguras", (ArrayList<? extends Parcelable>) AreaSeguraUtil.writeToParcelList(areaSeguraList));
        savedInstanceState.putParcelableArrayList("arreasSegurasRemovidas", (ArrayList<? extends Parcelable>) AreaSeguraUtil.writeToParcelList(arreasSegurasRemovidas));
        savedInstanceState.putParcelableArrayList("areasSegurasEditadas", (ArrayList<? extends Parcelable>) AreaSeguraUtil.writeToParcelList(areasSegurasEditadas));
        savedInstanceState.putString("pesquisa", pesquisa);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != getActivity().RESULT_OK){
            return;
        }

        Bundle bundle = data.getExtras();
        if(requestCode == DIALOG_AREA_SEGURA_REQUEST){

            // Identifica se uma área segura foi removida
            if(data.hasExtra("areaSeguraRemovida")){
                AreaSegura areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSeguraRemovida"));
                startDialogConfirmacao(areaSegura);

                // Identifica se uma área segura será editada
            }else if(data.hasExtra("areaSeguraMonitorado")){
                AreaSeguraMonitorado areaSeguraMonitorado = AreaSeguraMonitoradoUtil.writeToAraSeguraMonitorado(bundle.getParcelable("areaSeguraMonitorado"));
                if(areaSeguraMonitorado.isAtiva()){
                    areaSeguraMonitorado.setAtiva(false);
                    loader.setMessage(getString(R.string.inativando_area_segura));
                }else{
                    areaSeguraMonitorado.setAtiva(true);
                    loader.setMessage(getString(R.string.ativando_area_segura));
                }
                loader.show();
                new AlterarStatus(areaSeguraMonitorado).execute();

                // Identifica se uma área segura será editada
            }else if(data.hasExtra("areaSeguraEditar")){
                AreaSegura areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSeguraEditar"));
                Bundle editBundle = new Bundle();
                editBundle.putParcelable("areaSegura", AreaSeguraUtil.writeToParcel(areaSegura));
                Intent intent = new Intent(getActivity(), AreaSeguraActivity.class);
                intent.putExtras(editBundle);
                startActivityForResult(intent, EDIT_AREA_SEGURA_REQUEST);

                // Identifica a área segura a ser mostrada no mapa
            }else if(data.hasExtra("areaSeguraMostrar")){
                areaSeguraMostrar = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSeguraMostrar"));
                finishActivity();
            }

        }else if(requestCode == EDIT_AREA_SEGURA_REQUEST){

            // Identifica se uma área segura foi editada
            if(data.hasExtra("areaSegura")){
                AreaSegura areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSegura"));
                areasSegurasEditadas.add(areaSegura);
                areaSeguraAdapter.editAreaSegura(areaSegura);
            }

        }else if(requestCode == CONFIRMACAO_EXCLUIR_AREA_SEGURA_REQUEST){
            AreaSegura areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSegura"));
            loader.setMessage(getString(R.string.excluindo_area_segura));
            loader.show();
            new ExcluirAreaSegura(areaSegura).execute();
        }
    }

    /**
     * Classe responsável por fazer a exclusão de uma área segura
     */
    private class ExcluirAreaSegura extends AsyncTask<Void, Void, Void>{

        private AreaSegura areaSegura;
        private AreaSeguraMonitoradoService areaSeguraMonitoradoService;

        private ExcluirAreaSegura(AreaSegura areaSegura){
            this.areaSegura = areaSegura;
            areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(thisFragment.getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Executa a requisição HTTP para efetuar a exclusão da área segura no servidor
            HttpUtil.delete(CollabtrackApplication.API_PATH_AREA_SEGURA+"?id="+areaSegura.getId()+"&celular="+monitor.getCelular(), new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    excluirAreaSegura();
                }

                @Override
                public void onError(VolleyError error) {
                    loader.hide();
                }
            });

            return null;
        }

        /**
         * Método responsável por excluir a área segura do celular
         */
        private void excluirAreaSegura(){

            // Remove a área segura do banco de dados do celular
            areaSeguraService.delete(areaSegura.getId());
            // Remove a relação entre área segura e monitorado do banco de dados do celular
            areaSeguraMonitoradoService.delete(areaSegura.getId());
            // Remove a área segura da lista
            areaSeguraAdapter.removeAreaSegura(areaSegura.getId());
            // Adiciona a área segura na lista de removidas, para remover do mapa quando voltar a tela
            arreasSegurasRemovidas.add(areaSegura);

            // Remove a área segura excluída do array de editadas
            ListIterator iterator = areaSeguraList.listIterator();
            while (iterator.hasNext()){
                AreaSegura areaSeguraIterator = (AreaSegura) iterator.next();
                if(areaSeguraIterator.getId() == areaSegura.getId()){
                    areasSegurasEditadas.remove(areaSeguraIterator);
                }
            }

            // Esconde o loader
            loader.hide();

        }

    }

    private class AlterarStatus extends AsyncTask<Void, Void, Void>{

        private AreaSeguraMonitorado areaSeguraMonitorado;
        private AreaSeguraMonitoradoService areaSeguraMonitoradoService;
        private AlterarStatus(AreaSeguraMonitorado areaSeguraMonitorado){
            this.areaSeguraMonitorado = areaSeguraMonitorado;
            this.areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(thisFragment.getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpUtil.put(JsonUtil.toJson(this.areaSeguraMonitorado), CollabtrackApplication.API_PATH_AREA_SEGURA_MONITORADO, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    alterarStatus();
                }

                @Override
                public void onError(VolleyError error) {
                    loader.hide();
                }
            });

            return null;
        }

        private void alterarStatus(){
            this.areaSeguraMonitoradoService.update(this.areaSeguraMonitorado);

            // Esconde o loader
            loader.hide();
        }

    }

    private void pesquisarAreasSeguras() {
        areaSeguraList = areaSeguraService.search(pesquisa);

        areaSeguraAdapter = new AreaSeguraAdapter(getContext(), areaSeguraList, onClickAreaSegura());
        recyclerView.setAdapter(areaSeguraAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finishActivity();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    /**
     * Método responsável por finalizar a activity, passando os parâmetros de resposta
     */
    public void finishActivity(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("areasSegurasRemovidas", (ArrayList<? extends Parcelable>) AreaSeguraUtil.writeToParcelList(arreasSegurasRemovidas));
        bundle.putParcelableArrayList("areasSegurasEditadas", (ArrayList<? extends Parcelable>) AreaSeguraUtil.writeToParcelList(areasSegurasEditadas));
        bundle.putParcelable("areaSeguraMostrar", AreaSeguraUtil.writeToParcel(areaSeguraMostrar));
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    /**
     * Classe responsável por buscar o monitor autenticado e em seguida, buscar a relação entre o monitor autenticado e os monitorados apenas para os monitores no qual ele é principal
     */
    private class CarregarDadosParaPermissoes extends AsyncTask<Void, Void, Void>{

        private MonitorService monitoradoService;
        private MonitorMonitoradoService monitorMonitoradoService;
        private CarregarDadosParaPermissoes(){
            this.monitoradoService = new MonitorService(getActivity().getApplicationContext());
            this.monitorMonitoradoService = new MonitorMonitoradoService(getActivity().getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            monitor = monitoradoService.findAutenticado();
            monitorMonitorados = monitorMonitoradoService.findAll();
            return null;
        }

    }

    /**
     * Classe responsável por validar se a área segura está associada a um único monitorado e se o monitor áutenticado é o principal.
     * Também é responsável por iniciar o dialog com as ações da área segura
     */
    private class StartDialog extends AsyncTask<Void, Void, Void>{

        private AreaSeguraMonitoradoService areaSeguraMonitoradoService;
        private AreaSegura areaSegura;
        private List<AreaSeguraMonitorado> areaSeguraMonitorados;
        private StartDialog(AreaSegura areaSegura){
            this.areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(getActivity().getApplicationContext());
            this.areaSegura = areaSegura;
        }

        @Override
        protected Void doInBackground(Void... params) {
            this.areaSeguraMonitorados = areaSeguraMonitoradoService.findByAreaSegura(areaSegura.getId());
            this.startDialog();
            return null;
        }

        private void startDialog(){

            // Bundle
            Bundle bundle = new Bundle();

            // Área segura referente a ação a ser executada
            bundle.putParcelable("areaSegura", AreaSeguraUtil.writeToParcel(this.areaSegura));

            // Carrega o dialog
            AreaSeguraDialogFragment areaSeguraDialogFragment = new AreaSeguraDialogFragment();
            areaSeguraDialogFragment.setArguments(bundle);
            areaSeguraDialogFragment.setTargetFragment(thisFragment, DIALOG_AREA_SEGURA_REQUEST);
            areaSeguraDialogFragment.show(getFragmentManager(), CollabtrackApplication.DEBUG_TAG);

        }

    }

    /**
     * Método responsável por abrir o dialog de confirmação de exclusão de área seguras
     */
    private void startDialogConfirmacao(AreaSegura areaSegura){

        // Bundle
        Bundle bundle = new Bundle();

        // Área segura a ser excluida
        bundle.putParcelable("areaSegura", AreaSeguraUtil.writeToParcel(areaSegura));

        // Carrega o dialog
        AreaSeguraDialogExcluirFragment areaSeguraExcluirFragment = new AreaSeguraDialogExcluirFragment();
        areaSeguraExcluirFragment.setArguments(bundle);
        areaSeguraExcluirFragment.setTargetFragment(thisFragment, CONFIRMACAO_EXCLUIR_AREA_SEGURA_REQUEST);
        areaSeguraExcluirFragment.show(getFragmentManager(), CollabtrackApplication.DEBUG_TAG);

    }

}
