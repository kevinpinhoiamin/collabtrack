package br.senai.collabtrack.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.activity.MonitoradoActivity;
import br.senai.collabtrack.adapter.MonitorSpinnerAdapter;
import br.senai.collabtrack.adapter.MonitorSwitchAdapter;
import br.senai.collabtrack.component.Loader;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;
import br.senai.collabtrack.domain.to.DataFirebaseTO;
import br.senai.collabtrack.domain.to.MonitoradoTO;
import br.senai.collabtrack.domain.util.MonitorUtil;
import br.senai.collabtrack.domain.util.MonitoradoUtil;
import br.senai.collabtrack.domain.util.StatusUtil;
import br.senai.collabtrack.request.AlterarMonitoradoRequest;
import br.senai.collabtrack.request.BuscarMonitorRequest;
import br.senai.collabtrack.request.BuscarMonitoradoRequest;
import br.senai.collabtrack.request.CadastrarMonitoradoRequest;
import br.senai.collabtrack.request.HttpCallback;
import br.senai.collabtrack.service.MonitorMonitoradoService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.service.MonitoradoService;
import br.senai.collabtrack.service.StatusService;
import br.senai.collabtrack.util.ColorUtil;
import br.senai.collabtrack.util.JsonUtil;
import br.senai.collabtrack.util.TelefoneMaskUtil;
import petrov.kristiyan.colorpicker.ColorPicker;

public class MonitoradoFragment extends BaseFragment {

    // Este fragmento
    private MonitoradoFragment thisFragment = this;

    private TextView textViewDisclaimer;

    // Nome e celular
    private TextInputLayout nomeTextInputLayout;
    private EditText nomeEditText;
    private TextInputLayout celularTextInputLayout;
    private EditText celularEditText;
    private String nome;
    private boolean isUpdatingNumber;
    private long celular;

    // Cor do monitor
    private ImageView imageViewCorDisabled;
    private ImageView imageViewCor;
    private int imageViewCorSelecionada;

    // Monitor principal
    private MonitorSpinnerAdapter monitorSpinnerAdapter;
    private MaterialBetterSpinner monitorSpinner;

    // Monitores secundários
    private MonitorSwitchAdapter monitorSwitchAdapter;
    private RecyclerView recyclerViewMonitorados;

    // Botão de cadastro/edição
    private Button buttonCadastrar;
    private ProgressBar progressBarButtonCadastrar;

    // Monitor autenticado
    private Monitor monitorAutenticado;
    private MonitorService monitorService;

    // Monitor principal
    private Monitor monitorPrincipal;

    // Relação entre monitor e monitorado (cor)
    private MonitorMonitorado monitorMonitorado;

    // Lista de monitores
    private List<Monitor> monitorList;
    private List<Monitor> monitorListAssociado;
    private Monitor[] monitors;

    // Objeto de transferência
    private MonitoradoTO monitoradoTO;

    // Define se é cadastro ou edição
    private boolean cadastro;

    // Flag que define se é o monitor principal que está editando o registro
    private boolean edicaoPrincipal;

    // Loader
    private Loader loader;

    // BroadcasteReceiver para atualização dos dados
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monitor autenticado e monitor principal
        monitorService = new MonitorService(thisFragment.getContext());
        monitorAutenticado = monitorService.findAutenticado();

        // Loader
        loader = new Loader(getContext());
        loader.setTitle(getString(R.string.aguarde));
        loader.setMessage(getString(R.string.sincronizando_dados));

        // Objeto de tranferência
        monitoradoTO = new MonitoradoTO();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.hasExtra("acao") && intent.getIntExtra("acao", 0) == DataFirebaseTO.ACAO_REMOCAO_MONITORADO){
                    finalizar(true);
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CollabtrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitorado, viewGroup, false);

        // RecyclerView
        recyclerViewMonitorados = (RecyclerView) view.findViewById(R.id.recycler_view_monitorados);
        recyclerViewMonitorados.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMonitorados.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMonitorados.setHasFixedSize(false);

        // Disclaimer
        textViewDisclaimer                  = (TextView)                view.findViewById(R.id.text_view_disclaimer);

        // Nome
        nomeTextInputLayout                 = (TextInputLayout)         view.findViewById(R.id.text_input_layout_nome);
        nomeEditText                        = (EditText)                view.findViewById(R.id.edit_text_nome);
        nomeEditText.addTextChangedListener(onTextChangedListenerNome());

        // Celualr
        celularTextInputLayout              = (TextInputLayout)         view.findViewById(R.id.text_input_layout_celular);
        celularEditText                     = (EditText)                view.findViewById(R.id.edit_text_celular);
        celularEditText.addTextChangedListener(onTextChangedListenerCelular());

        // Cores padões do color picker
        if(savedInstanceState == null && imageViewCorSelecionada == 0){
            imageViewCorSelecionada = CollabtrackApplication.COLOR_PICKER_DEFAULT_COLOR;
        }else if(savedInstanceState != null && savedInstanceState.containsKey("imageViewCorSelecionada")){
            imageViewCorSelecionada = savedInstanceState.getInt("imageViewCorSelecionada");
        }

        // Iniciando o color picker
        imageViewCorDisabled                = (ImageView)               view.findViewById(R.id.image_view_cor_disabled);
        imageViewCor                        = (ImageView)               view.findViewById(R.id.image_view_cor);
        imageViewCor.setBackgroundColor(ColorUtil.getColor(imageViewCorSelecionada));
        imageViewCor.setOnClickListener(onClickShowColorPicker(imageViewCor));

        // Combo do monitor principal
        monitorSpinner                      = (MaterialBetterSpinner)   view.findViewById(R.id.spinner_monitor);
        monitorSpinner.setOnItemClickListener(onItemClickMonitorSpinner());
        monitorSpinner.setOnClickListener(onClickMonitorSpinner());

        // Lista de monitores secundários
        recyclerViewMonitorados             = (RecyclerView)            view.findViewById(R.id.recycler_view_monitorados);

        // Botão de cadastro
        progressBarButtonCadastrar          = (ProgressBar)             view.findViewById(R.id.progress_bar_button_cadastrar);
        buttonCadastrar                     = (Button)                  view.findViewById(R.id.button_cadastrar);
        buttonCadastrar.setOnClickListener(onClickCadastrar());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dados passados pelo bundle
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(intent.hasExtra("statusEditar")){
            Status status = StatusUtil.writeToStatus(bundle.getParcelable("statusEditar"));

            // Busca a cor
            MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(getContext());
            monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), status.getMonitorado().getId());
            if(monitorMonitorado == null){
                finalizar(true);
            }

            // Monitorado
            monitoradoTO.setMonitorado(monitorMonitorado.getMonitorado());

            // Botão de cadastro
            cadastro = false;
            buttonCadastrar.setText(getString(R.string.editar));
        }else{
            cadastro = true;
            buttonCadastrar.setText(getString(R.string.cadastrar));
            setMonitorPrincipal(monitorAutenticado);
        }

        // Ciclo de vida
        if(savedInstanceState == null){
            buscarMonitores();
        }else{
            monitorPrincipal = MonitorUtil.writeToMonitor(savedInstanceState.getParcelable("monitorPrincipal"));
            monitorList = MonitorUtil.writeToMonitorList(savedInstanceState.getParcelableArrayList("monitorList"));
            monitorListAssociado = MonitorUtil.writeToMonitorList(savedInstanceState.getParcelableArrayList("monitorListAssociado"));

            monitoradoTO.setMonitorPrincipal(monitorPrincipal);
            monitoradoTO.setMonitores(monitorListAssociado);
            preencherAdapter(true);
            associarMonitores();
        }

        // Preenche o formulário
        preencherFormulario();

    }

    @Override
    public void onSaveInstanceState(Bundle savedIstanceState) {
        super.onSaveInstanceState(savedIstanceState);
        // Salva o monitor que estava selecionado
        savedIstanceState.putParcelable("monitorPrincipal", MonitorUtil.writeToParcel(monitorPrincipal));
        // Salva as cores selecionadas para não perder ao girar a tela
        savedIstanceState.putInt("imageViewCorSelecionada", imageViewCorSelecionada);
        // Salva a lista de monitores
        savedIstanceState.putParcelableArrayList("monitorList", (ArrayList<? extends Parcelable>) MonitorUtil.writeToParcelList(monitorList));
        // Salva a lista de monitores associados
        savedIstanceState.putParcelableArrayList("monitorListAssociado", (ArrayList<? extends Parcelable>) MonitorUtil.writeToParcelList(monitorListAssociado));
    }

    // Método que busca todos os monitores do servidor
    private void buscarMonitores(){

        loader.show();

        new BuscarMonitorRequest(new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                monitorList = JsonUtil.toObjectList(response, Monitor[].class);
                if(monitoradoTO.getMonitorado() != null){
                    buscarMonitoresAssociados();
                }
                if(cadastro){
                    preencherAdapter(true);
                    setMonitorPrincipal(monitorAutenticado);
                }
                loader.hide();
            }

            @Override
            public void onError(VolleyError error) {
                loader.hide();
                desabilitarFormulario();
                desabilitarBotao();
            }
        }).execute();

    }

    // Método que busca os monitores associados ao monitor que está sendo editado
    private void buscarMonitoresAssociados(){

        loader.show();

        new BuscarMonitoradoRequest(monitoradoTO.getMonitorado().getId(), new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                monitoradoTO = (MonitoradoTO) JsonUtil.toObject(response, MonitoradoTO.class);
                monitorPrincipal = monitoradoTO.getMonitorPrincipal();
                monitorListAssociado = monitoradoTO.getMonitores();
                preencherAdapter(true);
                associarMonitores();
                if(!cadastro){
                    setMonitorPrincipal(monitorPrincipal);
                }
                loader.hide();
            }

            @Override
            public void onError(VolleyError error) {
                loader.hide();
                desabilitarFormulario();
                desabilitarBotao();
            }
        }).execute();

    }

    /**
     * Método que associa os monitores
     */
    private void associarMonitores(){
        if(monitoradoTO.getMonitorPrincipal() instanceof Monitor && monitoradoTO.getMonitores() != null && monitoradoTO.getMonitores().size() > 0) {
            desabilitarNaoPrincipal();
            monitorSwitchAdapter.habilitarMonitores(monitoradoTO.getMonitores());
        }
    }

    // Método responsável por preencher os dados do formulário
    private void preencherFormulario(){

        if(monitoradoTO instanceof MonitoradoTO && monitoradoTO.getMonitorado() != null && monitoradoTO.getMonitorado().getId() > 0){
            nomeEditText.setText(monitoradoTO.getMonitorado().getNome());
            celularEditText.setText(String.valueOf(monitoradoTO.getMonitorado().getCelular()));
        }

        if(monitorMonitorado instanceof MonitorMonitorado){
            imageViewCorSelecionada = monitorMonitorado.getCor();
            imageViewCor.setBackgroundColor(ColorUtil.getColor(imageViewCorSelecionada));
        }

    }

    /**
     * Método responsável por buscar todos os monitores no servidor
     */
    private void preencherAdapter(boolean recyclerView){

        if(monitorList != null && monitorList.size() > 0){
            monitors = new Monitor[monitorList.size()];
            for (int i = 0; i < monitorList.size(); i++) {
                monitors[i] = monitorList.get(i);
            }
        }

        monitorSpinnerAdapter = new MonitorSpinnerAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, monitors);
        monitorSpinner.setAdapter(monitorSpinnerAdapter);

        edicaoPrincipal = monitorPrincipal != null && monitorAutenticado.getId() == monitorPrincipal.getId();

        if (recyclerView) {
            monitorSwitchAdapter = new MonitorSwitchAdapter(getContext(), monitorList, onCheckedChangeMonitorSwitch(), edicaoPrincipal);
            recyclerViewMonitorados.setAdapter(monitorSwitchAdapter);
        }


    }

    private void setMonitorPrincipal(Monitor monitor){
        monitorPrincipal = monitor;

        if(monitorPrincipal == null){
            monitorPrincipal = monitorAutenticado;
        }
        if(monitorSpinnerAdapter instanceof MonitorSpinnerAdapter && monitorSpinner instanceof MaterialBetterSpinner && monitor instanceof Monitor){
            monitorSpinner.setText(monitorPrincipal.getNome());
        }
        if(monitorSwitchAdapter instanceof  MonitorSwitchAdapter){
            monitorSwitchAdapter.desabilitarMonitor(monitorPrincipal.getId());
        }
    }

    /**
     * Método responsável pela troca do monitor principal
     * @return Evento de troca de item no combo de monitor principal
     */
    private AdapterView.OnItemClickListener onItemClickMonitorSpinner(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMonitorPrincipal(monitors[position]);
            }
        };
    }

    private AdapterView.OnClickListener onClickMonitorSpinner()  {
        return new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                preencherAdapter(false);
                monitorSpinner.showDropDown();
            }
        };
    }

    /**
     * Método responsável pelo evento de alteração do switch do monitor
     * @return Instância do evento de troca do switch
     */
    private MonitorSwitchAdapter.MonitorOnCheckedChangeListener onCheckedChangeMonitorSwitch(){
        return new MonitorSwitchAdapter.MonitorOnCheckedChangeListener() {
            @Override
            public void onCheckedChangeMonitor(boolean isChecked, int idx) {
                monitorList.get(idx).setMarcado(isChecked);
            }
        };
    }

    private TextWatcher onTextChangedListenerNome(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validarNome()){
                    habilitarBotao();
                }else {
                    desabilitarBotao();
                }
            }
        };
    }

    private TextWatcher onTextChangedListenerCelular(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isUpdatingNumber) {
                    isUpdatingNumber = false;
                    return;
                }

                String result = TelefoneMaskUtil.clearFormating(s.toString());
                result = TelefoneMaskUtil.formatPhoneNumber(result);
                isUpdatingNumber = true;
                celularEditText.setText(result);
                celularEditText.setSelection(celularEditText.getText().length());

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validarCelular()){
                    habilitarBotao();
                }else {
                    desabilitarBotao();
                }
            }
        };
    }

    /**
     * Método responsável por validar o celular
     * @return TRUE caso o número de celular seja válido, FALSE caso contrário
     */
    private boolean validarCelular(){

        String celularClean = TelefoneMaskUtil.clearFormating(celularEditText.getText().toString());
        if(celularClean.equals("")){
            celularTextInputLayout.setError(getString(R.string.campo_obrigatorio));
        }else if(celularClean.length() != 10 && celularClean.length() != 11){
            celularTextInputLayout.setError(getString(R.string.preencha_corretamente_o_celular));
        }else{
            celularTextInputLayout.setErrorEnabled(false);
            celularTextInputLayout.setError(null);
            celular = Long.parseLong(celularClean);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por validar o nome
     * @return TRUE caso o nome seja válido, FALSE caso contrário
     */
    private boolean validarNome(){

        nome = nomeEditText.getText().toString();

        if(nome.equals("")){
            nomeTextInputLayout.setError(getString(R.string.campo_obrigatorio));
        }else{
            nomeTextInputLayout.setErrorEnabled(false);
            nomeTextInputLayout.setError(null);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por criar e mostrar o color picker
     * @param view // View que terá a cor de fundo trocada pela cor selecionada no picker
     * @return Evento de clique
     */
    private View.OnClickListener onClickShowColorPicker(final ImageView view){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Instância
                ColorPicker colorPicker = new ColorPicker(getContext());
                // Título
                colorPicker.setTitle(getString(R.string.selecione_uma_cor));
                // Cor padrão selecionada
                if(view.getId() == R.id.image_view_cor){
                    colorPicker.setDefaultColorButton(imageViewCorSelecionada);
                }
                // Evento de clique em uma das cores
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        view.setBackgroundColor(color);
                        if(view.getId() == R.id.image_view_cor){
                            imageViewCorSelecionada = color;
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                colorPicker.show();

            }
        };

    }

    /**
     * Método responsável por desabilitar o botão de cadastro
     */
    private void desabilitarBotao(){
        buttonCadastrar.setEnabled(false);
        progressBarButtonCadastrar.setVisibility(View.GONE);
        buttonCadastrar.setText(!cadastro ? getString(R.string.editar) : getString(R.string.cadastrar));
    }

    /**
     * Método responsável por habilitar o botão de cadastro
     */
    private void habilitarBotao(){

        String celularValidacao = TelefoneMaskUtil.clearFormating(celularEditText.getText().toString());
        if(!nomeEditText.getText().toString().equals("") && (celularValidacao.length() == 10 || celularValidacao.length() == 11)){
            buttonCadastrar.setEnabled(true);
        }
        progressBarButtonCadastrar.setVisibility(View.GONE);
        buttonCadastrar.setText(!cadastro ? getString(R.string.editar) : getString(R.string.cadastrar));
    }

    /**
     * Método utilizado para desabilitar os campos quando monitor secundário
     */
    private void desabilitarNaoPrincipal(){
        if(!edicaoPrincipal){
            textViewDisclaimer.setVisibility(View.VISIBLE);
            nomeEditText.setEnabled(false);
            nomeEditText.setFocusable(false);
            nomeEditText.setFocusableInTouchMode(false);
            celularEditText.setEnabled(false);
            celularEditText.setFocusable(false);
            celularEditText.setFocusableInTouchMode(false);
            monitorSpinner.setEnabled(false);
            monitorSpinner.setFocusable(false);
            monitorSpinner.setFocusableInTouchMode(false);
            monitorSpinner.setClickable(false);
            monitorSpinner.setDropDownHeight(0);
        }
    }

    /**
     * Método responsável por desabilitar o formulário e mostrar o loader
     */
    private void desabilitarFormulario(){

        try{
            nomeEditText.setEnabled(false);
            nomeEditText.setFocusable(false);
            nomeEditText.setFocusableInTouchMode(false);
            celularEditText.setEnabled(false);
            celularEditText.setFocusable(false);
            celularEditText.setFocusableInTouchMode(false);
        }catch (NullPointerException e){}
        imageViewCorDisabled.setVisibility(View.VISIBLE);
        imageViewCor.setClickable(false);
        imageViewCor.setOnClickListener(null);
        monitorSpinner.setEnabled(false);
        monitorSpinner.setFocusable(false);
        monitorSpinner.setFocusableInTouchMode(false);
        monitorSpinner.setClickable(false);
        monitorSpinner.setOnItemClickListener(null);
        monitorSpinner.setOnClickListener(null);
        buttonCadastrar.setEnabled(false);
        buttonCadastrar.setText("");
        progressBarButtonCadastrar.setVisibility(View.VISIBLE);
    }

    /**
     * Método responsável por habilitar o formulário e esconder o loader
     */
    private void habilitarFormulario(){
        // Se for o monitor principal, permite habilitar
        if(edicaoPrincipal){
            try{
                nomeEditText.setEnabled(true);
                nomeEditText.setFocusable(true);
                nomeEditText.setFocusableInTouchMode(true);
                celularEditText.setEnabled(true);
                celularEditText.setFocusable(true);
                celularEditText.setFocusableInTouchMode(true);
            }catch (NullPointerException e){}
            monitorSpinner.setEnabled(true);
            monitorSpinner.setFocusable(true);
            monitorSpinner.setFocusableInTouchMode(true);
            monitorSpinner.setClickable(true);
            monitorSpinner.setOnItemClickListener(onItemClickMonitorSpinner());
            monitorSpinner.setOnClickListener(onClickMonitorSpinner());
        }
        imageViewCorDisabled.setVisibility(View.GONE);
        imageViewCor.setClickable(true);
        imageViewCor.setOnClickListener(onClickShowColorPicker(imageViewCor));
        buttonCadastrar.setEnabled(true);
        buttonCadastrar.setText(!cadastro ? getString(R.string.editar) : getString(R.string.cadastrar));
        progressBarButtonCadastrar.setVisibility(View.GONE);
    }

    /**
     * Método responsável pelo clique no botão de cadastro
     * @return Instância do evento de clique
     */
    private View.OnClickListener onClickCadastrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Desabilita o formulário
                desabilitarFormulario();

                // Monta a instância do monitorado
                final Monitorado monitorado = new Monitorado();
                monitorado.setNome(nome);
                monitorado.setCelular(celular);

                // Cadastro
                if(cadastro){

                    // Instância do objeto de tranferência
                    monitoradoTO = new MonitoradoTO();
                    monitoradoTO.setMonitorado(monitorado);
                    monitoradoTO.setMonitorPrincipal(monitorPrincipal);
                    monitoradoTO.setMonitorAutenticado(monitorAutenticado);
                    monitoradoTO.setMonitores(monitorList);
                    monitoradoTO.setCor(imageViewCorSelecionada);

                    // Requisição HTTP
                    new CadastrarMonitoradoRequest(monitoradoTO, new HttpCallback() {
                        @Override
                        public void onSuccess(String response) {

                            boolean marcado = false;
                            if(monitorList != null && monitorList.size() > 0){
                                for (Monitor monitor : monitorList){
                                    if(monitor.isMarcado() && monitorAutenticado.getId() == monitor.getId()){
                                        marcado = true;
                                    }
                                }
                            }

                            // Se o monitor autenticado for o principal ou se o monitor autenticado está marcado
                            if(monitorAutenticado.getId() == monitorPrincipal.getId() || marcado){

                                // Converte o JSON de resposta para a instância do objeto
                                monitoradoTO = (MonitoradoTO) JsonUtil.toObject(response, MonitoradoTO.class);

                                // Cria a instância dos objetos que serão cadastrados
                                MonitorMonitorado monitorMonitorado = new MonitorMonitorado();
                                monitorMonitorado.setMonitor(monitorAutenticado);
                                monitorMonitorado.setMonitorado(monitoradoTO.getMonitorado());
                                monitorMonitorado.setPrincipal(monitorAutenticado.getId() == monitoradoTO.getMonitorPrincipal().getId());
                                monitorMonitorado.setAtivo(true);
                                monitorMonitorado.setCor(imageViewCorSelecionada);

                                // Classes de persistência local
                                MonitoradoService monitoradoService = new MonitoradoService(getContext());
                                MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(getContext());
                                StatusService statusService = new StatusService(getContext());

                                // Cadastra os objetos no banco de dados local
                                monitoradoService.save(monitoradoTO.getMonitorado());
                                monitorMonitoradoService.save(monitorMonitorado);
                                statusService.saveOrUpdate(monitoradoTO.getStatus());

                                finalizar(false);
                            } else {
                                finalizar(true);
                            }

                        }

                        @Override
                        public void onError(VolleyError error) {
                            habilitarFormulario();
                        }
                    }).execute();

                }else {

                    monitorado.setId(monitorMonitorado.getMonitorado().getId());

                    // Edição de monitor secundário
                    if(!edicaoPrincipal){
                        monitoradoTO.setMonitorPrincipal(null);
                        monitoradoTO.setMonitorAutenticado(monitorAutenticado);
                        List<Monitor> monitorSecundario = new ArrayList<>();
                        monitorSecundario.add(monitorAutenticado);
                        monitoradoTO.setMonitores(monitorSecundario);
                        monitoradoTO.setMonitorado(monitorado);
                        monitoradoTO.setCor(imageViewCorSelecionada);

                    // Edição do monitor principal
                    }else{
                        monitoradoTO.setMonitorado(monitorado);
                        monitoradoTO.setMonitorPrincipal(monitorPrincipal);
                        monitoradoTO.setMonitorAutenticado(monitorAutenticado);
                        monitoradoTO.setMonitores(monitorList);
                        monitoradoTO.setCor(imageViewCorSelecionada);
                    }

                    // Requisição HTTP
                    new AlterarMonitoradoRequest(monitoradoTO, new HttpCallback() {
                        @Override
                        public void onSuccess(String response) {

                            // Instâncias da classe de serviço
                            // MonitoradoService monitoradoService = new MonitoradoService(getContext());
                            // MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(getContext());

                            // Edição de monitor secundário
                            if(!edicaoPrincipal){
                                /*
                                MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), monitoradoTO.getMonitorado().getId());
                                monitorMonitorado.setCor(monitoradoTO.getCor());
                                monitorMonitoradoService.update(monitorMonitorado);
                                */
                                updateCor();
                                finalizar(false);

                            // Se foi alterado o monitor principal
                            }else if(edicaoPrincipal && monitoradoTO.getMonitorPrincipal().getId() != monitorAutenticado.getId()){

                                boolean marcado = false;
                                if(monitorList != null && monitorList.size() > 0){
                                    for (Monitor monitor : monitorList){
                                        if(monitor.isMarcado() && monitorAutenticado.getId() == monitor.getId()){
                                            marcado = true;
                                        }
                                    }
                                }

                                if(!marcado) {
                                    MonitoradoService.delete(monitoradoTO.getMonitorado().getId());
                                    finalizar(marcado ? false : true);
                                } else {
                                    /*
                                    MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), monitoradoTO.getMonitorado().getId());
                                    monitorMonitorado.setCor(monitoradoTO.getCor());
                                    monitorMonitoradoService.update(monitorMonitorado);
                                    */
                                    updateDados();
                                    updateCor();
                                    finalizar(false);
                                }

                            // Edição do monitor principal
                            }else{
                                /*
                                Monitorado monitorado = monitoradoService.find(monitoradoTO.getMonitorado().getId());
                                monitorado.setNome(monitoradoTO.getMonitorado().getNome());
                                monitorado.setCelular(monitoradoTO.getMonitorado().getCelular());
                                monitoradoService.update(monitorado);
                                */
                                updateDados();

                                /*
                                MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), monitoradoTO.getMonitorado().getId());
                                monitorMonitorado.setCor(monitoradoTO.getCor());
                                monitorMonitoradoService.update(monitorMonitorado);
                                */
                                updateCor();

                                finalizar(false);
                            }

                        }

                        @Override
                        public void onError(VolleyError error) {
                            habilitarFormulario();
                        }
                    }).execute();

                }

            }
        };
    }

    private void updateDados() {
        MonitoradoService monitoradoService = new MonitoradoService(getContext());
        Monitorado monitorado = monitoradoService.find(monitoradoTO.getMonitorado().getId());
        monitorado.setNome(monitoradoTO.getMonitorado().getNome());
        monitorado.setCelular(monitoradoTO.getMonitorado().getCelular());
        monitoradoService.update(monitorado);
    }

    private void updateCor() {
        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(getContext());
        MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitorAutenticado.getId(), monitoradoTO.getMonitorado().getId());
        monitorMonitorado.setCor(monitoradoTO.getCor());
        monitorMonitorado.setPrincipal(monitoradoTO.getMonitorPrincipal() != null && monitoradoTO.getMonitorPrincipal().getId() == monitorAutenticado.getId());
        monitorMonitoradoService.update(monitorMonitorado);
    }

    /**
     * Método responsável por finalizar a activity, passando a área segura cadastrada/editada para apresentar na lista
     */
    private void finalizar(boolean remover){

        ((MonitoradoActivity) getActivity()).hideKeyboard();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("monitorado", MonitoradoUtil.writeToParcel(monitoradoTO.getMonitorado()));
        bundle.putBoolean("remover", remover);
        getActivity().setResult(Activity.RESULT_OK, intent);
        intent.putExtras(bundle);
        getActivity().finish();
    }

}
