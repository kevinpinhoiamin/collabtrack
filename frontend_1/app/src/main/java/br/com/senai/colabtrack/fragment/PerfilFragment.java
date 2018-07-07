package br.com.senai.colabtrack.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Callback;

import java.io.File;
import java.io.IOException;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.activity.HomeActivity;
import br.com.senai.colabtrack.activity.MainActivity;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.request.AlterarMonitorRequest;
import br.com.senai.colabtrack.request.CadastrarMonitorRequest;
import br.com.senai.colabtrack.request.EnviarFotoMonitorRequest;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.request.SairRequest;
import br.com.senai.colabtrack.request.SincronizarDadosRequest;
import br.com.senai.colabtrack.request.SincronizarMensagemRequest;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.util.CameraUtil;
import br.com.senai.colabtrack.util.ConnectionUtil;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.TelefoneMaskUtil;

/**
 * Created by kevin on 28/09/17.
 */

public class PerfilFragment extends BaseFragment {

    private MainActivity mainActivity;

    // Dados do monitor
    private TextInputLayout nomeTextInputLayout;
    private EditText nomeEditText;
    private TextInputLayout celularTextInputLayout;
    private EditText celularEditText;

    // Máscara de celular
    private boolean isUpdatingNumber;
    private long celular;

    // Delay para enviar os dados do monitor
    private Handler handler;
    private Runnable runnable;

    // Token de autenticação
    private EditText tokenEditText;

    // Foto de perfil
    private View viewImageViewMonitor;
    private ProgressBar progressBarImageViewMonitor;
    private ImageView imageViewMonitor;

    // Câmera
    private CameraUtil camera = new CameraUtil();

    // Botão para resicronizar os dados com o servidor
    private ProgressBar progressBarButtonResincronizarDados;
    private Button buttonResincronizarDados;

    // Botão para logout
    private Button buttonSair;

    // onitor
    private MonitorService monitorService;
    private Monitor monitor;

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity            = (MainActivity) getActivity();

        monitorService          = new MonitorService(mainActivity);
        monitor                 = monitorService.findAutenticado();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        if(savedInstanceState != null){
            // Recupera o estado da câmera
            camera.onCreate(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nomeTextInputLayout                     = (TextInputLayout) mainActivity.findViewById(R.id.text_input_layout_nome);
        nomeEditText                            = (EditText)        mainActivity.findViewById(R.id.edit_text_nome);
        celularTextInputLayout                  = (TextInputLayout) mainActivity.findViewById(R.id.text_input_layout_celular);
        celularEditText                         = (EditText)        mainActivity.findViewById(R.id.edit_text_celular);

        tokenEditText                           = (EditText)        mainActivity.findViewById(R.id.edit_text_token);

        viewImageViewMonitor                    =                   mainActivity.findViewById(R.id.view_image_view_monitor);
        progressBarImageViewMonitor             = (ProgressBar)     mainActivity.findViewById(R.id.progress_bar_image_view_monitor);
        imageViewMonitor                        = (ImageView)       mainActivity.findViewById(R.id.image_view_monitor);
        imageViewMonitor.setOnClickListener(onClickTrocarFoto());

        progressBarButtonResincronizarDados     = (ProgressBar)     mainActivity.findViewById(R.id.progress_bar_button_resincronizar_dados);
        buttonResincronizarDados                = (Button)          mainActivity.findViewById(R.id.button_resincronizar_dados);
        buttonResincronizarDados.setOnClickListener(onClickResincronizarDados());

        buttonSair                              = (Button)          mainActivity.findViewById(R.id.button_sair);
        buttonSair.setOnClickListener(onClickSair());

        preencherFormulario();
        carregarFoto(true);

        nomeEditText.addTextChangedListener(onTextChangedListenerNomeCelular(false));
        celularEditText.addTextChangedListener(onTextChangedListenerNomeCelular(true));

        handler = new Handler();
        runnable = alterarDadosMonitor();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        camera.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(ColabTrackApplication.DEBUG_TAG, "--------------> 1");

        if (resultCode == Activity.RESULT_OK) {

            Log.d(ColabTrackApplication.DEBUG_TAG, "--------------> 2");

            if(requestCode == REQUEST_CODE_CAMERA){

                Log.d(ColabTrackApplication.DEBUG_TAG, "--------------> 3");

                // Resize da imagem
                Bitmap bitmap = camera.getBitmap(200, 200);

                if (bitmap != null) {
                    // Salva arquivo neste tamanho
                    camera.save(bitmap);

                    // Atualiza imagem do monitor
                    imageViewMonitor.setImageBitmap(bitmap);

                    // Faz a requisição para enviar a foto para o servidor
                    enviarFoto();
                }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamera();
        }

    }

    /**
     * Método responsável pelo evento de alterar os texto dos inputs de nome e celular
     * @return Instância do evento de mudança de texto
     */
    private TextWatcher onTextChangedListenerNomeCelular(final boolean ehCelular){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (ehCelular) {
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

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(validarNome()
                        && validarCelular()
                        && (!nomeEditText.getText().equals(monitor.getNome()) || celular != monitor.getCelular())){

                    // Remove as callbacks anteriores se alterou o texto novamente
                    handler.removeCallbacks(runnable);

                    // Executa depois de três segundos
                    handler.postDelayed(runnable, 2000);

                }
            }
        };
    }

    /**
     * Método responsável por alterar os dados do monitor no servidor e no celular
     * @return
     */
    private Runnable alterarDadosMonitor(){

        return new Runnable() {
            @Override
            public void run() {

                monitor.setNome(nomeEditText.getText().toString());
                monitor.setCelular(celular);

                new AlterarMonitorRequest(monitor, new HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        monitorService.update(monitor);
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                }).execute();

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

        if(nomeEditText.getText().toString().equals("")){
            nomeTextInputLayout.setError(getString(R.string.campo_obrigatorio));
        }else{
            nomeTextInputLayout.setError(null);
            return true;
        }

        return false;

    }

    /**
     * Método responsável por preencher os dados no formulário
     */
    private void preencherFormulario(){
        nomeEditText.setText(monitor.getNome());
        celularEditText.setText(TelefoneMaskUtil.formatPhoneNumber(String.valueOf(monitor.getCelular())));
        celular = monitor.getCelular();
        tokenEditText.setText(monitor.getTokenAutenticacao());
    }

    /**
     * Método responsável por desabilitar o botão "Resincronizar dados"
     */
    private void desabilitarBotaoResincronizarDados(){
        buttonResincronizarDados.setText("");
        buttonResincronizarDados.setEnabled(false);
        progressBarButtonResincronizarDados.setVisibility(View.VISIBLE);
    }

    /**
     * Método responsável por habilitar o botão "Resincronizar dados"
     */
    private void habilitarBotaoResincronizarDados(){
        buttonResincronizarDados.setText(R.string.resincronizar_dados);
        buttonResincronizarDados.setEnabled(true);
        progressBarButtonResincronizarDados.setVisibility(View.GONE);
    }

    /**
     * Método responsável pelo evento de clique o botão "Resincronizar dados"
     * @return Evento de clique
     */
    private View.OnClickListener onClickResincronizarDados(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                salvarMonitor();
                if (!ConnectionUtil.isNetworkAvailable()) {
                    Toast.makeText(ColabTrackApplication.getContext(), getString(R.string.voce_nao_esta_conectado), Toast.LENGTH_LONG).show();
                } else if(!ConnectionUtil.isWifi()){
                    Toast.makeText(ColabTrackApplication.getContext(), getString(R.string.voce_nao_esto_no_wifi), Toast.LENGTH_LONG).show();
                } else {
                    desabilitarBotaoResincronizarDados();
                    sincronizarDados();
                    sincronizarMensagem();
                }
            }
        };
    }

    /**
     * Método responsável pelo evento de clique do botão "Sair"
     * @return
     */
    private View.OnClickListener onClickSair(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SairRequest(monitor).execute();
                // Limpa a base de dados local e volta para a tela inicial
                ColabTrackApplication.getContext().deleteDatabase(ColabTrackApplication.DATABASE);
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
    }

    /**
     * Método responsável por salvar o monitors
     */
    private void salvarMonitor(){
        MonitorService monitorService = new MonitorService(getContext());
        monitorService.save(monitor);
    }

    private void sincronizarMensagem(){
        new SincronizarMensagemRequest(monitor.getCelular()){}.execute();
    }

    /**
     * Método responsável por fazer a sincronização de dados do monitor com o servidor
     */
    private void sincronizarDados(){
        new SincronizarDadosRequest(monitor.getCelular(), monitor.getId(), new SincronizarDadosRequest.FinalizarSincronizacao() {
            @Override
            public void onFinalizarSincronizacao(boolean sincronizacaAreaSeguraMonitoradoCompleta, boolean sincronizacaoMonitorMonitorado, boolean sincronizacaoStatus) {

                if(sincronizacaAreaSeguraMonitoradoCompleta && sincronizacaoMonitorMonitorado && sincronizacaoStatus){
                    carregarFoto(true);
                    habilitarBotaoResincronizarDados();
                }

            }

            @Override
            public void onError() {
                habilitarBotaoResincronizarDados();
            }
        }).execute();

    }

    /**
     * Método responsável pelo evento de clique para trocar a foto
     * @return Instância do evento de clique
     */
    private View.OnClickListener onClickTrocarFoto(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(
                        ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION);
                }else if(ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE_PERMISSION);
                }else if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION);
                }else{
                    abrirCamera();
                }

            }
        };
    }

    private void abrirCamera(){

        if(
                ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {

            // Se clicar na imagem de header, tira a foto
            // Cria o o arquivo no sdcard
            long ms = System.currentTimeMillis();
            String fileName = String.format("monitor_%s_%s.jpg", monitor.getId(), ms);
            // A classe Camera cria a intent e o arquivo no sdcard.
            try {
                Intent intent = camera.open(getContext(), fileName);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_abrir_camera), Toast.LENGTH_LONG);
                }
            } catch (IOException e) {
                Toast.makeText(getContext(), getString(R.string.error_abrir_camera), Toast.LENGTH_LONG);
            }

        }

    }

    private void enviarFoto(){

        File foto = camera.getFile();

        if(foto != null && foto.exists()){

            desabilitarFoto();

            new EnviarFotoMonitorRequest(this.monitor, foto, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    carregarFoto(false);
                }

                @Override
                public void onError(VolleyError error) {

                }
            }).execute();
        }

    }

    /**
     * Método responsável por mostrar um loader no container da foto
     */
    private void desabilitarFoto(){
        if(imageViewMonitor.getHeight() > 0){
            viewImageViewMonitor.setMinimumHeight(imageViewMonitor.getHeight());
        }
        viewImageViewMonitor.setVisibility(View.VISIBLE);
        progressBarImageViewMonitor.setVisibility(View.VISIBLE);
    }

    /**
     * Método responsável por remover o loader do container da foto
     */
    private void habilitarFoto(){
        viewImageViewMonitor.setVisibility(View.INVISIBLE);
        progressBarImageViewMonitor.setVisibility(View.INVISIBLE);
    }

    /**
     * Método responsável por carregar a foto no container
     */
    private void carregarFoto(boolean cache){

        desabilitarFoto();

        // ColabTrackApplication.API_PATH_MONITOR+"/picture/"+monitor.getCelular()
        HttpUtil.getImage(
                ColabTrackApplication.API_PATH_MONITOR+"/picture/"+monitor.getCelular(),
                imageViewMonitor,
                new Callback() {

            @Override
            public void onSuccess() {
                habilitarFoto();
            }

            @Override
            public void onError() {
                imageViewMonitor.setImageResource(R.drawable.default_user);
                habilitarFoto();
            }

        }, cache);

    }

}
