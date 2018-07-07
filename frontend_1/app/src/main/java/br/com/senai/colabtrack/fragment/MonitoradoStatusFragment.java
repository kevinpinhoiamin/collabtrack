package br.com.senai.colabtrack.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.activity.MainActivity;
import br.com.senai.colabtrack.activity.MonitoradoActivity;
import br.com.senai.colabtrack.adapter.MonitoradoStatusAdapter;
import br.com.senai.colabtrack.component.Loader;
import br.com.senai.colabtrack.domain.Localizacao;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.MonitorMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.domain.Status;
import br.com.senai.colabtrack.domain.to.DataFirebaseTO;
import br.com.senai.colabtrack.domain.to.MonitoradoTO;
import br.com.senai.colabtrack.domain.util.LocalizacaoUtil;
import br.com.senai.colabtrack.domain.util.MonitoradoUtil;
import br.com.senai.colabtrack.domain.util.StatusUtil;
import br.com.senai.colabtrack.fragment.dialog.StatusDialogFragment;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.service.LocalizacaoService;
import br.com.senai.colabtrack.service.MonitorMonitoradoService;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.service.StatusService;
import br.com.senai.colabtrack.util.ColorUtil;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

/**
 * Created by kevin on 08/08/17.
 */

public class MonitoradoStatusFragment extends BaseFragment {

    // Atributos da classe
    private List<Status> statusList;
    private Monitor monitorAutenticado;
    private Loader loader;

    // Fragment
    private MonitoradoStatusFragment thisFragment = this;

    // Classe de serviço
    private StatusService statusService;

    // Activity
    private MainActivity mainActivity;

    // Recycler View e Adapter
    private RecyclerView recyclerView;
    private MonitoradoStatusAdapter statusAdapter;

    // Request code
    private static final int DIALOG_STATUS_REQUEST = 1;
    private static final int EDICAO_MONITOR_REQUEST = 2;

    // Itens do menu
    private MenuItem menuItemMonitoramento;
    private MenuItem menuItemLocalizacao;

    // BroadcasteReceiver para atualização do status
    private BroadcastReceiver broadcastReceiver;

    // Botão para abrir o formulário de cadastro
    private FloatingActionButton fabCadastroMonitorado;

    // Request para cadastro/edição de monitorado
    private static final int REQUEST_CODE_MONITORADO = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        loader = new Loader(getContext());
        loader.setTitle(getString(R.string.aguarde));

        statusService = new StatusService(getContext());

        mainActivity = (MainActivity) getActivity();

        new BuscarStatus().execute();

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Se o adapter não estiver setado, não pega as informações do broadcast
                if(statusAdapter == null){
                    return;
                }

                if(intent.hasExtra("localizacoes")){
                    alterarIconeLocalizacao(true);
                }else if(intent.hasExtra("status")){
                    Status status = StatusUtil.writeToStatus(intent.getParcelableExtra("status"));
                    if(statusAdapter instanceof MonitoradoStatusAdapter){
                        statusAdapter.editarStatus(status);
                    }
                }else if(intent.hasExtra("acao") && intent.hasExtra("monitorado_to")){
                    MonitoradoTO monitoradoTO = MonitoradoUtil.writeToMonitoradoTO(intent.getParcelableExtra("monitorado_to"));

                    Status status = monitoradoTO.getStatus();
                    status.setCor(monitoradoTO.getCor());
                    status.setPrincipal(intent.getBooleanExtra("principal", false));

                    switch (intent.getIntExtra("acao", 0)){
                        case DataFirebaseTO.ACAO_CADASTRO_MONITORADO:
                            status.setAtivo(true);
                            statusAdapter.alterarStatus(monitoradoTO.getStatus());
                            break;
                        case DataFirebaseTO.ACAO_EDICAO_MONITORADO:
                            status.setAtivo(intent.getBooleanExtra("ativo", false));
                            statusAdapter.alterarStatus(monitoradoTO.getStatus());
                            break;
                    }

                }else if(intent.hasExtra("acao") && intent.getIntExtra("acao", 0) == DataFirebaseTO.ACAO_REMOCAO_MONITORADO && intent.hasExtra("id_monitorado")){
                    statusAdapter.removerMonitorado(intent.getLongExtra("id_monitorado", 0));
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ColabTrackApplication.LOCALIZACAO_BROADCAST_RECEIVER);
        intentFilter.addAction(ColabTrackApplication.STATUS_BROADCAST_RECEIVER);
        intentFilter.addAction(ColabTrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitorado_status, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabCadastroMonitorado = (FloatingActionButton) mainActivity.findViewById(R.id.fab_cadastro_monitorado);
        fabCadastroMonitorado.setOnClickListener(onClickCadastrarMonitorado());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuItemMonitoramento = menu.findItem(R.id.nav_item_monitoramento);
        menuItemLocalizacao = menu.findItem(R.id.nav_item_localizacao);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_item_monitoramento:

                loader.setMessage(getString(item.getTitle().equals(getString(R.string.iniciar_monitoramento)) ? R.string.iniciando_monitoramento : R.string.finalizando_monitoramento));
                loader.show();

                new Monitorar(null, item.getTitle().equals(getString(R.string.iniciar_monitoramento))).execute();

                break;

            case R.id.nav_item_localizacao:

                loader.setMessage(getString(R.string.limpando_localizacoes));
                loader.show();

                new LimparLocalizacoes().execute();
                break;
        }

        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != getActivity().RESULT_OK){
            return;
        }

        Bundle bundle = data.getExtras();
        if(requestCode == DIALOG_STATUS_REQUEST){

            if(data.hasExtra("statusEditar")){
                Intent intent = new Intent(getActivity(), MonitoradoActivity.class);
                intent.putExtras(bundle);

                startActivityForResult(intent, REQUEST_CODE_MONITORADO);
            }else if(data.hasExtra("monitorado")){
                Monitorado monitorado = MonitoradoUtil.writeToMonitorado(bundle.getParcelable("monitorado"));
                boolean remover = bundle.getBoolean("remover");

                if(monitorado == null){
                    return;
                }

                if(!remover && monitorado.getId() > 0){
                    Status status = statusService.findByMonitorado(monitorado.getId());
                    if(status != null){
                        statusAdapter.alterarStatus(status);
                    }
                }else {
                    statusAdapter.removerMonitorado(monitorado);
                }
            }else if(data.hasExtra("statusAtivar") || data.hasExtra("statusInativar")){
                Status status = StatusUtil.writeToStatus(bundle.getParcelable(data.hasExtra("statusAtivar") ? "statusAtivar" : "statusInativar"));

                loader.setMessage(getString(status.isAtivo() ? R.string.inativando_monitorado : R.string.ativando_monitorado));
                loader.show();

                new AlterarStatus(status).execute();
            }else if(data.hasExtra("statusMostrar")){
                mainActivity.changeMenuItem(mainActivity.MENU_ITEM_MAPA);
                GoogleMapFragment mapFragment = new GoogleMapFragment();
                mapFragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, mapFragment, mainActivity.FRAGMENT_TAG_MAPA).commitAllowingStateLoss();
            }else if(data.hasExtra("statusLimparLocalizacao")){
                Status status = StatusUtil.writeToStatus(bundle.getParcelable("statusLimparLocalizacao"));

                loader.setMessage(getString(R.string.limpando_localizacoes));
                loader.show();

                new LimparLocalizacao(status.getMonitorado().getId()).execute();
            }else if(data.hasExtra("statusIniciarMonitoramento") || data.hasExtra("statusFinalizarMonitoramento")){
                loader.setMessage(getString(data.hasExtra("statusIniciarMonitoramento") ? R.string.iniciar_monitoramento : R.string.finalizar_monitoramento));
                loader.show();

                Status status = StatusUtil.writeToStatus(bundle.getParcelable(data.hasExtra("statusIniciarMonitoramento") ? "statusIniciarMonitoramento" : "statusFinalizarMonitoramento"));
                new Monitorar(status, data.hasExtra("statusIniciarMonitoramento")).execute();
            }

        }

    }

    private void alterarIconeMonitoramento(boolean iniciouMonitoramento){

        if(menuItemMonitoramento == null){
            return;
        }

        menuItemMonitoramento.setTitle(iniciouMonitoramento ? R.string.finalizar_monitoramento : R.string.iniciar_monitoramento);
        menuItemMonitoramento.setIcon(iniciouMonitoramento ?  R.drawable.ic_notification_active : R.drawable.ic_notification_inactive);
    }

    private void alterarIconeLocalizacao(boolean temLocalizacoes){
        menuItemLocalizacao.setVisible(temLocalizacoes);
    }

    private MonitoradoStatusAdapter.StatusOnClickListener onClickStatus(){
        return new MonitoradoStatusAdapter.StatusOnClickListener() {
            @Override
            public void onClickStatus(View view, Status status) {
                new StatusDialog(status).execute();
            }
        };
    }

    private class BuscarStatus extends AsyncTask<Void, Void, Void>{

        private MonitorService monitorService;
        private BuscarStatus(){
            this.monitorService = new MonitorService(getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            monitorAutenticado = this.monitorService.findAutenticado();
            statusList = statusService.findAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new AlterarIconeMonitoramento().execute();
            new AlterarIconeLocalizacao().execute();
            statusAdapter = new MonitoradoStatusAdapter(getContext(), statusList, onClickStatus());
            recyclerView.setAdapter(statusAdapter);
        }
    }

    private class AlterarIconeMonitoramento extends AsyncTask<Void, Void, Void>{

        private MonitorMonitoradoService monitorMonitoradoService;
        private boolean iniciouMonitoramento;
        private AlterarIconeMonitoramento(){
            this.monitorMonitoradoService = new MonitorMonitoradoService(getContext());
            this.iniciouMonitoramento = false;

        }

        @Override
        protected Void doInBackground(Void... params) {

            if(statusList != null && statusList.size() > 0){
                for (br.com.senai.colabtrack.domain.Status status : statusList){
                    MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), status.getMonitorado().getId());
                    if(monitorMonitorado.isEmMonitoramento()){
                        this.iniciouMonitoramento = true;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            alterarIconeMonitoramento(this.iniciouMonitoramento);
        }
    }

    private class AlterarIconeLocalizacao extends AsyncTask<Void, Void, List<Localizacao>>{

        private LocalizacaoService localizacaoService;
        private AlterarIconeLocalizacao(){
            this.localizacaoService = new LocalizacaoService(getContext());
        }

        @Override
        protected List<Localizacao> doInBackground(Void... params) {
            return localizacaoService.findAll();
        }

        @Override
        protected void onPostExecute(List<Localizacao> localizacaoList) {
            alterarIconeLocalizacao(localizacaoList != null && localizacaoList.size() > 0);
        }
    }

    private class StatusDialog extends AsyncTask<Void, Void, Void>{

        private MonitorMonitoradoService monitorMonitoradoService;
        private LocalizacaoService localizacaoService;
        private br.com.senai.colabtrack.domain.Status status;
        private boolean emMonitoramento;
        private boolean temLocalizacao;
        private StatusDialog(br.com.senai.colabtrack.domain.Status status){
            this.status = status;
            this.monitorMonitoradoService = new MonitorMonitoradoService(getContext());
            this.localizacaoService = new LocalizacaoService(getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), status.getMonitorado().getId());
            this.emMonitoramento = monitorMonitorado.isEmMonitoramento();

            List<Localizacao> localizacaoList = localizacaoService.findByMonitorado(status.getMonitorado().getId());
            this.temLocalizacao = localizacaoList != null && localizacaoList.size() > 0;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("status", StatusUtil.writeToParcel(status));
            bundle.putBoolean("emMonitoramento", this.emMonitoramento);
            bundle.putBoolean("temLocalizacao", this.temLocalizacao);

            StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
            statusDialogFragment.setArguments(bundle);
            statusDialogFragment.setTargetFragment(thisFragment, DIALOG_STATUS_REQUEST);
            statusDialogFragment.show(getFragmentManager(), ColabTrackApplication.DEBUG_TAG);
        }
    }

    private class AlterarStatus extends AsyncTask<Void, Void, Void>{

        private MonitorMonitoradoService monitorMonitoradoService;
        private br.com.senai.colabtrack.domain.Status status;
        private MonitorMonitorado monitorMonitorado;

        private AlterarStatus(br.com.senai.colabtrack.domain.Status status){
            this.monitorMonitoradoService = new MonitorMonitoradoService(getContext());
            this.status = status;
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Busca a classe de relação entreo o monitor autenticado e o monitorado do status
            monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), status.getMonitorado().getId());
            // Ativa/inativa o status da relação
            monitorMonitorado.setAtivo(status.isAtivo() ? false : true);
            monitorMonitorado.setEmMonitoramento(monitorMonitorado.isAtivo() ? monitorMonitorado.isEmMonitoramento(): false);

            HttpUtil.put(JsonUtil.toJson(monitorMonitorado), ColabTrackApplication.API_PATH_MONITOR_MONITORADO, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    monitorMonitorado = (MonitorMonitorado) JsonUtil.toObject(response, MonitorMonitorado.class);
                    alterarMonitorMonitorado();
                }

                @Override
                public void onError(VolleyError error) {
                    loader.hide();
                }
            });

            return null;
        }

        private void alterarMonitorMonitorado(){
            monitorMonitoradoService.update(monitorMonitorado);

            status.setAtivo(monitorMonitorado.isAtivo());
            status.setEmMonitoramento(monitorMonitorado.isAtivo() ? monitorMonitorado.isEmMonitoramento() : false);
            statusAdapter.alterarStatus(status);

            loader.hide();

            new AlterarIconeMonitoramento().execute();
        }

    }

    private class Monitorar extends AsyncTask<Void, Void, Void>{

        private MonitorMonitoradoService monitorMonitoradoService;
        private MonitorMonitorado monitorMonitorado;
        private br.com.senai.colabtrack.domain.Status status;
        private boolean monitorar;
        private Monitorar(br.com.senai.colabtrack.domain.Status status, boolean monitorar){
            this.status = status;
            this.monitorar = monitorar;
            this.monitorMonitoradoService = new MonitorMonitoradoService(getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(status != null){
                monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), status.getMonitorado().getId());
            }else{
                monitorMonitorado = new MonitorMonitorado();
                monitorMonitorado.setMonitor(monitorAutenticado);
            }
            monitorMonitorado.setEmMonitoramento(monitorar);

            HttpUtil.put(JsonUtil.toJson(monitorMonitorado), ColabTrackApplication.API_PATH_MONITOR_MONITORADO + "/monitoramento", new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    finalizar();
                }

                @Override
                public void onError(VolleyError error) {
                    loader.hide();
                }
            });

            return null;
        }

        private void finalizar(){

            if(status != null){
                monitorMonitoradoService.update(monitorMonitorado);
                statusAdapter.alterarMonitoramento(monitorMonitorado);
            }else{
                List<MonitorMonitorado> monitorMonitorados = monitorMonitoradoService.findAll();
                for (MonitorMonitorado monitorMonitoradoIteracao : monitorMonitorados){
                    if(!monitorMonitoradoIteracao.isAtivo()){
                        continue;
                    }

                    monitorMonitoradoIteracao.setEmMonitoramento(monitorMonitorado.isEmMonitoramento());
                    monitorMonitoradoService.update(monitorMonitoradoIteracao);
                    statusAdapter.alterarMonitoramento(monitorMonitoradoIteracao);
                }
            }

            loader.hide();

            new AlterarIconeMonitoramento().execute();

        }

    }

    private class LimparLocalizacoes extends AsyncTask<Void, Void, Void>{

        private LocalizacaoService localizacaoService;
        private LimparLocalizacoes(){
            this.localizacaoService = new LocalizacaoService(getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            localizacaoService.deletarLocalizacoes();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loader.hide();
            alterarIconeLocalizacao(false);
        }
    }

    private class LimparLocalizacao extends AsyncTask<Void, Void, Void>{

        private LocalizacaoService localizacaoService;
        private long idMonitorado;
        private LimparLocalizacao(long idMonitorado){
            this.localizacaoService = new LocalizacaoService(getContext());
            this.idMonitorado = idMonitorado;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            localizacaoService.deletarLocalizacoes(idMonitorado);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loader.hide();
            new AlterarIconeLocalizacao().execute();
        }
    }

    /**
     * Método responsável pelo evento de clique no FAB de cadastro de monitorado
     * @return Instância do evento de clique
     */
    private View.OnClickListener onClickCadastrarMonitorado(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mainActivity, MonitoradoActivity.class), REQUEST_CODE_MONITORADO);
            }
        };
    }

}
