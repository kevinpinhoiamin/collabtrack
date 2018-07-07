package br.com.senai.colabtrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.request.BuscarMonitorRequest;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.request.SincronizarDadosRequest;
import br.com.senai.colabtrack.request.SincronizarMensagemRequest;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.util.JsonUtil;
import br.com.senai.colabtrack.util.TelefoneMaskUtil;

public class SignInActivity extends BaseActivity {

    private SignInActivity thisActivity;

    private TextInputLayout celularTextInputLayout;
    private EditText celularEditText;
    private TextInputLayout tokenTextInputLayout;
    private EditText tokenEditText;

    private ProgressBar progressBarButtonEntrar;
    private Button buttonEntrar;

    private boolean isUpdatingNumber;
    private long celular;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        thisActivity = this;

        celularTextInputLayout  = (TextInputLayout) findViewById(R.id.text_input_layout_celular);
        celularEditText         = (EditText)        findViewById(R.id.edit_text_celular);
        celularEditText.addTextChangedListener(onTextChangedListenerCelular());
        tokenTextInputLayout    = (TextInputLayout) findViewById(R.id.text_input_layout_token);
        tokenEditText           = (EditText)        findViewById(R.id.edit_text_token);
        tokenEditText.addTextChangedListener(onTextChangedListenerToken());

        progressBarButtonEntrar = (ProgressBar)     findViewById(R.id.progress_bar_button_entrar);
        buttonEntrar            = (Button)          findViewById(R.id.button_entrar);
        buttonEntrar.setOnClickListener(onClickEntrar());

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

    private TextWatcher onTextChangedListenerToken(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validarToken()){
                    habilitarBotao();
                }else{
                    desabilitarBotao();
                }
            }
        };
    }

    /**
     * Método responsável pelo evento de clique no botão de entrar
     * @return Evento de clique
     */
    private View.OnClickListener onClickEntrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarToken() && validarCelular()){
                    buscarMonitor();
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
     * Método responsável por validar o token
     * @return TRUE caso válido, FALSE caso inválido
     */
    private boolean validarToken(){

        token = tokenEditText.getText().toString();

        if(token.equals("")){
            tokenTextInputLayout.setError(getString(R.string.campo_obrigatorio));
        }else if(token.length() != 6){
            tokenTextInputLayout.setError(getString(R.string.token_6_caracteres));
        }else{
            tokenTextInputLayout.setErrorEnabled(false);
            tokenTextInputLayout.setError(null);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por desabilitar o formulário
     */
    private void desabilitarFormulario(){
        buttonEntrar.setText("");
        buttonEntrar.setEnabled(false);
        progressBarButtonEntrar.setVisibility(View.VISIBLE);
        celularTextInputLayout.setEnabled(false);
        celularTextInputLayout.setFocusable(false);
        celularTextInputLayout.setFocusableInTouchMode(false);
        tokenTextInputLayout.setEnabled(false);
        tokenTextInputLayout.setFocusable(false);
        tokenTextInputLayout.setFocusableInTouchMode(false);
    }

    private void habilitarFormulario(){
        buttonEntrar.setText(getString(R.string.entrar));
        buttonEntrar.setEnabled(true);
        progressBarButtonEntrar.setVisibility(View.GONE);
        celularTextInputLayout.setEnabled(true);
        celularTextInputLayout.setFocusable(true);
        celularTextInputLayout.setFocusableInTouchMode(true);
        tokenTextInputLayout.setEnabled(true);
        tokenTextInputLayout.setFocusable(true);
        tokenTextInputLayout.setFocusableInTouchMode(true);
    }

    /**
     * Método responsável por desabilitar o botão de entrar
     */
    private void desabilitarBotao(){
        buttonEntrar.setEnabled(false);
    }

    /**
     * Método responsável por habilitar o botão de entrar
     */
    private void habilitarBotao(){

        String celularValidacao = TelefoneMaskUtil.clearFormating(celularEditText.getText().toString());
        if(!token.equals("") && token.length() == 6 && (celularValidacao.length() == 10 || celularValidacao.length() == 11)){
            buttonEntrar.setEnabled(true);
        }

    }

    /**
     * Método responsável por buscar o monitor no servidor
     */
    private void buscarMonitor(){
        // Desabilita o formulário e mostra o loader
        desabilitarFormulario();

        new BuscarMonitorRequest(celular, token, new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                // Instância do monitor e da classe de servço
                Monitor monitor = (Monitor) JsonUtil.toObject(response, Monitor.class);
                MonitorService service = new MonitorService(ColabTrackApplication.getContext());
                // Cadastra o monitor no banco de dados do celular
                service.save(monitor);
                // Chama a função para sincronizar os dados com o servidor
                sincronizarDados(monitor);

                sincronizarMensagem(monitor);
            }

            @Override
            public void onError(VolleyError error) {
                habilitarFormulario();
            }
        }).execute();
    }

    public void sincronizarMensagem(Monitor monitor){
        new SincronizarMensagemRequest(monitor.getCelular()){}.execute();
    }

    /**
     * Método responsável por fazer a sincronização de dados do monitor com o servidor
     * @param monitor Instância do monitor que efetuou a autenticação
     */
    private void sincronizarDados(Monitor monitor){
        new SincronizarDadosRequest(monitor.getCelular(), monitor.getId(), new SincronizarDadosRequest.FinalizarSincronizacao() {
            @Override
            public void onFinalizarSincronizacao(boolean sincronizacaAreaSeguraMonitoradoCompleta, boolean sincronizacaoMonitorMonitorado, boolean sincronizacaoStatus) {

                if(sincronizacaAreaSeguraMonitoradoCompleta && sincronizacaoMonitorMonitorado && sincronizacaoStatus){
                    Intent intent = new Intent(thisActivity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }

            @Override
            public void onError() {
                ColabTrackApplication.getContext().deleteDatabase(ColabTrackApplication.DATABASE);
                habilitarFormulario();
            }
        }).execute();

    }

}
