package br.senai.collabtrack.client.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import br.senai.collabtrack.client.R;
import br.senai.collabtrack.client.broadcast.BatteryStatusReceiver;
import br.senai.collabtrack.client.object.Monitorado;
import br.senai.collabtrack.client.services.LocationService;
import br.senai.collabtrack.client.services.MonitoradoService;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.util.HttpCallback;
import br.senai.collabtrack.client.util.HttpUtil;

public class SignInActivity extends AppCompatActivity {


    private SignInActivity thisActivity;

    private TextInputLayout celularTextInputLayout;
    private EditText celularEditText;
    private TextInputLayout tokenTextInputLayout;
    private EditText tokenEditText;

    private ProgressBar progressBarButtonEntrar;
    private Button buttonEntrar;

    private long celular;
    private boolean celularMascaraCompleta;
    private String token = "";

    static boolean loginOk = false;

    private static Monitorado monitorado = new Monitorado();

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.viewLoginCalled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInActivity.loginOk = false;

        thisActivity = this;

        celularTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_celular);
        celularEditText = (EditText) findViewById(R.id.edit_text_celular);
        celularEditText.addTextChangedListener(onTextChangedListenerCelular());
        tokenTextInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout_token);
        tokenEditText = (EditText) findViewById(R.id.edit_text_token);
        tokenEditText.addTextChangedListener(onTextChangedListenerToken());

        progressBarButtonEntrar = (ProgressBar) findViewById(R.id.progress_bar_button_entrar);
        buttonEntrar = (Button) findViewById(R.id.button_entrar);
        buttonEntrar.setOnClickListener(onClickEntrar());

        aplicarMascaraCelular();

    }

    private void aplicarMascaraCelular() {

        final MaskedTextChangedListener maskedTextChangedListener = new MaskedTextChangedListener(
                "([00]) [00000]-[0000]",
                false,
                celularEditText,
                null,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NotNull String extractedValue) {
                        celularMascaraCompleta = maskFilled;
                        if (extractedValue != null && !extractedValue.equals("")) {
                            celular = Long.parseLong(extractedValue);
                        }
                    }
                }
        );

        celularEditText.addTextChangedListener(maskedTextChangedListener);
        celularEditText.setOnFocusChangeListener(maskedTextChangedListener);

    }

    private TextWatcher onTextChangedListenerCelular() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validarCelular()) {
                    habilitarBotao();
                } else {
                    desabilitarBotao();
                }
            }
        };
    }

    private TextWatcher onTextChangedListenerToken() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validarToken()) {
                    habilitarBotao();
                } else {
                    desabilitarBotao();
                }
            }
        };
    }

    /**
     * Método responsável pelo evento de clique no botão de entrar
     *
     * @return Evento de clique
     */
    private View.OnClickListener onClickEntrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarToken() && validarCelular()) {
                    buscarMonitor();
                }
            }
        };
    }

    /**
     * Método responsável por validar o celular
     *
     * @return TRUE caso o número de celular seja válido, FALSE caso contrário
     */
    private boolean validarCelular() {

        if (celularEditText.getText().toString().equals("")) {
            celularTextInputLayout.setError("Campo obrigatório");
        } else if (!celularMascaraCompleta) {
            celularTextInputLayout.setError(("Número de celular incorreto"));
        } else {
            celularTextInputLayout.setErrorEnabled(false);
            celularTextInputLayout.setError(null);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por validar o token
     *
     * @return TRUE caso válido, FALSE caso inválido
     */
    private boolean validarToken() {

        token = tokenEditText.getText().toString();

        if (token.isEmpty()) {
            tokenTextInputLayout.setError("Campo obrigatório");
        } else if (token.length() != 6) {
            tokenTextInputLayout.setError("O Token deve ter no mínimo 6 caracteres");
        } else {
            tokenTextInputLayout.setErrorEnabled(false);
            tokenTextInputLayout.setError(null);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por desabilitar o formulário
     */
    private void desabilitarFormulario() {
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

    private void habilitarFormulario() {
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
    private void desabilitarBotao() {
        buttonEntrar.setEnabled(false);
    }

    /**
     * Método responsável por habilitar o botão de entrar
     */
    private void habilitarBotao() {
        if (!token.equals("") && token.length() == 6 && celularMascaraCompleta) {
            buttonEntrar.setEnabled(true);
        }
    }

    /**
     * Método responsável por buscar o monitor no servidor
     */

    public static boolean encontrouMonitorado = false;
    public static boolean encontrouMonitor = false;

    private void buscarMonitor() {

        desabilitarFormulario();

        final String celularMonitorado = celularEditText.getText().toString().replaceAll("\\D", "");
        final String tokenMonitor = tokenEditText.getText().toString();

        final MonitoradoService monitoradoService = new MonitoradoService();
/*
        if (numCelMonitorado == null || numCelMonitorado.length() < 10 || numCelMonitorado.length() > 11)
            throw new AppInfoException("Número de celular inválido!\n\n" +
                    "O número deve conter 10 ou 11 dígitos");
                    */

        HttpUtil.get((MonitoradoService.REST_MONITORADO + celularMonitorado), new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                if  (response == null || response.isEmpty()){
                    habilitarFormulario();
                    Application.toast(thisActivity, "Monitorado não cadastrado");
                }

                try {
                    JSONObject monitoradoJSONObject = new JSONObject(response);

                    Application.log("json do monitorado -> " + response);

                    monitorado = new Monitorado();
                    monitorado.setId(monitoradoJSONObject.getLong("id"));
                    monitorado.setCelular(monitoradoJSONObject.getLong("celular"));

                    Application.log("Após setar -> " + monitorado.getId());

                } catch (JSONException e) {
                    Application.log(e);
                    Application.toast(thisActivity, "Falha interna do aplicativo");
                    habilitarFormulario();
                }

                HttpUtil.get(MonitoradoService.REST_MONITOR + "?celular_monitorado=" + celularMonitorado + "&token_autenticacao=" + tokenMonitor, new HttpCallback() {
                    @Override
                    public void onSuccess(String response) {

                        if (response == null || response.isEmpty()){
                            Application.toast(thisActivity, "Token inválido");
                            habilitarFormulario();
                        }

                        JSONObject monitorJSONObject = null;
                        try {
                            monitorJSONObject = new JSONObject(response);

                            monitorado.setIdMonitor(monitorJSONObject.getLong("id"));
                            monitorado.setCelularMonitor(monitorJSONObject.getLong("celular"));

                            Application.log("Após setar monitor -> " + monitorado.getId());
                            monitoradoService.deleteAll();
                            monitoradoService.save(monitorado);

                            //Solicita permissões necessárias
                            if (ContextCompat.checkSelfPermission(thisActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }

                            if (ContextCompat.checkSelfPermission(thisActivity.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(thisActivity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
                            }

                            Application.inicializaServicos();

                            loginOk = true;
                            thisActivity.finish();

                            SignInActivity.loginOk = true;

                            MainActivity.setMonitorPhoto();

                            if (monitoradoService.emMonitoramento()){
                                Application.log("Monitoramento ativo");
                                startService(new Intent(getBaseContext(), BatteryStatusReceiver.class));
                                startService(new Intent(getBaseContext(), LocationService.class));
                            } else {
                                Application.log("Monitoramento desligado");
                            }
                        } catch (JSONException e) {
                            Application.log(e);
                            Application.toast(thisActivity, "Falha interna do aplicativo");
                            habilitarFormulario();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        habilitarFormulario();
                    }
                });
            }

            @Override
            public void onError(VolleyError error) {
                habilitarFormulario();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Application.log(this.getClass().getSimpleName() + " -> " + "onDestroy");
        super.onDestroy();
    }
}
