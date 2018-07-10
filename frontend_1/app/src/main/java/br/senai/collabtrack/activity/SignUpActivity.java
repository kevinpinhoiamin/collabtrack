package br.senai.collabtrack.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.util.MonitorUtil;
import br.senai.collabtrack.request.CadastrarMonitorRequest;
import br.senai.collabtrack.request.HttpCallback;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;
import br.senai.collabtrack.util.TelefoneMaskUtil;

public class SignUpActivity extends BaseActivity {

    private SignUpActivity thisActivity;

    private TextInputLayout nomeTextInputLayout;
    private EditText nomeEditText;
    private TextInputLayout celularTextInputLayout;
    private EditText celularEditText;

    private ProgressBar cadastrarEntrarButtonProgressBar;
    private Button cadastrarEntrarButton;

    private LinearLayout tokenAutenticacaoLayout;
    private TextView tokenAutenticacaoTextView;

    private String nome = "";
    private boolean isUpdatingNumber;
    private long celular;

    private Monitor monitor;
    private MonitorService monitorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        thisActivity = this;

        nomeTextInputLayout                 = (TextInputLayout) findViewById(R.id.text_input_layout_nome);
        nomeEditText                        = (EditText)        findViewById(R.id.edit_text_nome);
        nomeEditText.addTextChangedListener(onTextChangedListenerNome());
        celularTextInputLayout              = (TextInputLayout) findViewById(R.id.text_input_layout_celular);
        celularEditText                     = (EditText)        findViewById(R.id.edit_text_celular);
        celularEditText.addTextChangedListener(onTextChangedListenerCelular());

        cadastrarEntrarButtonProgressBar    = (ProgressBar)     findViewById(R.id.progress_bar_button_cadastrar_entrar);
        cadastrarEntrarButton               = (Button)          findViewById(R.id.button_cadastrar_entrar);
        cadastrarEntrarButton.setOnClickListener(onClickCadastrar());

        tokenAutenticacaoLayout             = (LinearLayout)    findViewById(R.id.token_autenticacao_layout);
        tokenAutenticacaoTextView           = (TextView)        findViewById(R.id.token_autenticacao_text_view);

        if(savedInstanceState != null && savedInstanceState.containsKey("monitor")){
            monitor = MonitorUtil.writeToMonitor(savedInstanceState.getParcelable("monitor"));
            mostrarDadosAutenticacao();
        }else{
            monitor = new Monitor();
        }
        monitorService = new MonitorService(thisActivity);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("monitor", MonitorUtil.writeToParcel(monitor));
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
     * Método responsável pelo evento de clique no botão de cadastro
     * @return Evento de clique
     */
    private View.OnClickListener onClickCadastrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = nomeEditText.getText().toString();

                if(validarCelular() && validarNome()){
                    hideKeyboard();
                    cadastrar();
                }
            }
        };
    }

    /**
     * Método responsável pelo evento de clique no botão de autenticação
     * @return Evento de clique
     */
    private View.OnClickListener onClickEntrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = CollabtrackApplication.getContext().getSharedPreferences("APP_INFORMATION", Context.MODE_PRIVATE);
                String token_app = preferences.getString("token_app", "");

                MonitorService monitorService = new MonitorService(CollabtrackApplication.getContext());
                Monitor monitor = monitorService.findAutenticado();
                if(monitor != null) {
                    Log.d("Token para eviar1: ", "Token: " + token_app);
                    monitor.setToken(token_app);
                    HttpUtil.put(JsonUtil.toJson(monitor), CollabtrackApplication.API_PATH_MONITOR, new HttpCallback() {

                        @Override
                        public void onSuccess(String response) {

                        }

                        @Override
                        public void onError(VolleyError error) {
                        }
                    });
                }

                Intent intent = new Intent(thisActivity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
     * Método responsável por desabilitar o formulário
     */
    private void desabilitarFormulario(){
        cadastrarEntrarButton.setText("");
        cadastrarEntrarButton.setEnabled(false);
        cadastrarEntrarButtonProgressBar.setVisibility(View.VISIBLE);
        nomeTextInputLayout.setEnabled(false);
        nomeTextInputLayout.setFocusable(false);
        nomeTextInputLayout.setFocusableInTouchMode(false);
        celularTextInputLayout.setEnabled(false);
        celularTextInputLayout.setFocusable(false);
        celularTextInputLayout.setFocusableInTouchMode(false);
    }

    /**
     * Método responsável por habilitar o formulário
     */
    private void habilitarFormulario(){
        cadastrarEntrarButton.setText(R.string.cadastrese);
        cadastrarEntrarButton.setEnabled(true);
        cadastrarEntrarButtonProgressBar.setVisibility(View.GONE);
        nomeTextInputLayout.setEnabled(true);
        nomeTextInputLayout.setFocusable(true);
        nomeTextInputLayout.setFocusableInTouchMode(true);
        celularTextInputLayout.setEnabled(true);
        celularTextInputLayout.setFocusable(true);
        celularTextInputLayout.setFocusableInTouchMode(true);
    }

    /**
     * Método responsável por desabilitar o botão de cadastro
     */
    private void desabilitarBotao(){
        cadastrarEntrarButton.setEnabled(false);
    }

    /**
     * Método responsável por habilitar o botão de cadastro
     */
    private void habilitarBotao(){

        String celularValidacao = TelefoneMaskUtil.clearFormating(celularEditText.getText().toString());
        if(!nome.equals("") && (celularValidacao.length() == 10 || celularValidacao.length() == 11)){
            cadastrarEntrarButton.setEnabled(true);
        }

    }

    /**
     * Método responsável por habilitar o formulário após efetuar o cadastro com sucesso
     */
    private void habilitarFormularioAutenticacao(){
        cadastrarEntrarButton.setEnabled(true);
        cadastrarEntrarButtonProgressBar.setVisibility(View.GONE);
    }

    /**
     * Método responsável por efetuar o cadastro do monitor e alterar o layout da tela para autenticação
     */
    private void cadastrar(){

        // Desabilita o formulário
        desabilitarFormulario();

        monitor.setNome(nome);
        monitor.setCelular(celular);

        new CadastrarMonitorRequest(monitor, new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                monitor = (Monitor) JsonUtil.toObject(response, Monitor.class);
                monitorService.save(monitor);

                mostrarDadosAutenticacao();
            }

            @Override
            public void onError(VolleyError error) {
                habilitarFormulario();
            }
        }).execute();

    }

    private void mostrarDadosAutenticacao(){

        if(monitor instanceof Monitor
                && monitor.getNome()!= null
                && monitor.getCelular() > 0
                && monitor.getTokenAutenticacao() != null){

            nomeEditText.setEnabled(false);
            nomeEditText.setFocusable(false);
            nomeEditText.setFocusableInTouchMode(false);
            celularEditText.setEnabled(false);
            celularEditText.setFocusable(false);
            celularEditText.setFocusableInTouchMode(false);

            tokenAutenticacaoLayout.setVisibility(View.VISIBLE);
            tokenAutenticacaoTextView.setText(monitor.getTokenAutenticacao());

            cadastrarEntrarButton.setText(R.string.entrar);
            cadastrarEntrarButton.setOnClickListener(onClickEntrar());

            habilitarFormularioAutenticacao();

        }else{
            habilitarFormulario();
        }

    }

}
