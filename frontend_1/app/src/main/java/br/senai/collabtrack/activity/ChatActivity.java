package br.senai.collabtrack.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.adapter.ChatAdapter;
import br.senai.collabtrack.domain.Chat;
import br.senai.collabtrack.domain.Mensagem;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.to.DataFirebaseTO;
import br.senai.collabtrack.domain.to.MensagemTO;
import br.senai.collabtrack.fragment.dialog.MensagemDialogExcluirFragment;
import br.senai.collabtrack.request.BuscarMonitorRequest;
import br.senai.collabtrack.request.HttpCallback;
import br.senai.collabtrack.service.MensagemService;
import br.senai.collabtrack.service.MonitorMonitoradoService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.sql.MensagemSQL;
import br.senai.collabtrack.util.ConnectionUtil;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

public class ChatActivity extends BaseActivity {

    private static final String PATH_NAME_AUDIO = "/audio/";
    private List<Chat> lista = null;
    private ListView ltwChats = null;
    private List<Monitor> monitores = null;
    private String nomeMonitorado;
    private long idMonitorado;
    private boolean principal;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nomeMonitorado = bundle.getString("nomeMonitorado");
        MonitorService monitorService = new MonitorService(getBaseContext());
        Monitor monitorAuth = monitorService.findAutenticado();
        idMonitorado = getIntent().getExtras().getLong("idMonitorado");

        MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(getBaseContext());
        principal = monitorMonitoradoService.find(monitorAuth.getId(), idMonitorado).isPrincipal();

        buscaMonitoresParaChat(monitorAuth.getId(), idMonitorado);

        final AudioRecorder audioRecorder = new AudioRecorder();

        final MediaRecorder[] mediaRecorder = new MediaRecorder[1];
        final String PATH_DIR_AUDIO = getTempFile("Collabtrack");

        setUpToolbar(nomeMonitorado);
        setIdToolbar(150);
        setUpHomeButton();

        String title = getSupportActionBar().getTitle().toString();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        MenuInflater menuInflater = getMenuInflater();

        ltwChats = (ListView) findViewById(R.id.ltwChats);
        refreshMensagems();

        final ImageButton btnEnviar = (ImageButton) findViewById(R.id.btnEnviar);
        final ImageButton btnAudio = (ImageButton) findViewById(R.id.btnAudio);

        if(principal == false ){
            btnEnviar.setVisibility(View.VISIBLE);
            btnAudio.setVisibility(View.INVISIBLE);
        }else{
            btnEnviar.setVisibility(View.INVISIBLE);
            btnAudio.setVisibility(View.VISIBLE);
        }

        final EditText txtMensagem = (EditText) findViewById(R.id.txtMensagem);
        txtMensagem.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtMensagem.getText().length() > 0 || principal == false ){
                    btnEnviar.setVisibility(View.VISIBLE);
                    btnAudio.setVisibility(View.INVISIBLE);
                }else{
                    btnEnviar.setVisibility(View.INVISIBLE);
                    btnAudio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtMensagem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), "Enviar: "+txtMensagem.getText(), Toast.LENGTH_SHORT).show();
            if(txtMensagem.getText().length() > 0){
                new EnviarMensagem(txtMensagem.getText().toString()).execute();
                txtMensagem.setText("");
            }
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionUtil.isNetworkAvailable()){
                    if(txtMensagem.getText().length() > 0){
                        new EnviarMensagem(txtMensagem.getText().toString()).execute();
                        txtMensagem.setText("");
                    }
                }else{
                    Toast.makeText(CollabtrackApplication.getContext(), getString(R.string.sem_conexao_com_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAudio.setOnTouchListener(new View.OnTouchListener(){
            public static final String BASENAME = "lucas";

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                File output= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),BASENAME);
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(ConnectionUtil.isNetworkAvailable()) {
                            mediaRecorder[0] = new MediaRecorder();
                            mediaRecorder[0].setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder[0].setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            mediaRecorder[0].setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            mediaRecorder[0].setOutputFile(PATH_DIR_AUDIO + "/minhagravacao.mp3");
                            try {
                                mediaRecorder[0].prepare();
                                mediaRecorder[0].start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        try {
                            if(ConnectionUtil.isNetworkAvailable()){
                                mediaRecorder[0].stop();
                                mediaRecorder[0].reset();
                                mediaRecorder[0].release();

                                new EnviarAudio(PATH_DIR_AUDIO+"/minhagravacao.mp3").execute();
                            }else{
                                Toast.makeText(CollabtrackApplication.getContext(), getString(R.string.sem_conexao_com_internet), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });
    }

    public String getTempFile(String dirExtra) {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + dirExtra);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
            folder.setExecutable(true);
            folder.setReadable(true);
            folder.setWritable(true);
        }
        if (success) {
            return folder.getAbsolutePath();
        } else {
            return null;
        }
    }

    private class EnviarAudio extends AsyncTask<Object, Object, Void> {
        Intent it = getIntent();
        String diretorioAudio;
        MensagemService mensagemService = new MensagemService(getApplicationContext());
        MonitorService monitorService;
        Monitor monitor;
        Monitorado monitorado;
        Long idMensagem;

        private Mensagem mensagem;

        private EnviarAudio(String dir) {
            Mensagem mensagemTexto = new Mensagem();
            mensagemTexto.setMensagem("Audio");
            this.mensagem = mensagemTexto;

            monitorado = new Monitorado();
            monitor = new Monitor();

            diretorioAudio = dir;
            monitorado.setId(it.getExtras().getLong("idMonitorado")) ;


            monitorService = new MonitorService(getApplicationContext());
            monitor = monitorService.findAutenticado();

            mensagem.setMonitor(monitor);
            mensagem.setData("");
            mensagem.setMonitorado(monitorado);
            mensagem.setTipo(2);
            idMensagem = mensagemService.save(mensagem);
            refreshMensagems();
        }

        @Override
        protected Void doInBackground(Object... voids) {
            String url = CollabtrackApplication.API_PATH_MENSAGEM + "/audio";
            monitorService = new MonitorService(getApplicationContext());
            monitor = monitorService.findAutenticado();

            Ion.with(CollabtrackApplication.getContext())
                    .load(url)
                    .setHeader(HttpUtil.getBasicAuthenticationParam(), HttpUtil.getBasicAuthenticationHash())
                    .setMultipartParameter("monitor", String.valueOf(monitor.getId()))
                    .setMultipartParameter("monitorado", String.valueOf(it.getExtras().getLong("idMonitorado")))
                    .setMultipartFile("audio", new File(diretorioAudio))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            MensagemService mensagemService = new MensagemService(getApplicationContext());
                            MensagemTO mensagemRetorno = (MensagemTO) JsonUtil.toObject(result.toString(), MensagemTO.class);
                            Mensagem mensagemEdit = new Mensagem();
                            mensagemEdit.setId(mensagemRetorno.getId());
                            mensagemEdit.setData(mensagemRetorno.getData().toString());

                            InputStream in = null;
                            OutputStream out = null;
                            File src = new File(diretorioAudio);
                            File dst = new File(getTempFile("Collabtrack"+File.separator+monitorado.getId())+"/"+String.valueOf(mensagemEdit.getId())+".mp3");

                            try {
                                in = new FileInputStream(src);
                                out = new FileOutputStream(dst);

                                IOUtils.copy(in, out);

                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            mensagemService.updateId(mensagemEdit, idMensagem);
                            refreshMensagems();
                        }
                    });
            return null;
        }
    }

    private class EnviarMensagem extends AsyncTask<Void, Void, Void>{
        Intent it = getIntent();
        private Mensagem mensagem;

        final EditText txtMensagem = (EditText) findViewById(R.id.txtMensagem);
        MensagemService mensagemService = new MensagemService(getApplicationContext());
        MonitorService monitorService;
        Monitor monitor;
        Monitorado monitorado;
        Long idMensagem;

        private EnviarMensagem(String mensagemTxt) {

            Mensagem mensagemTexto = new Mensagem();
            mensagemTexto.setMensagem(mensagemTxt);
            this.mensagem = mensagemTexto;

            monitor = new Monitor();
            monitorado = new Monitorado();

            monitorService = new MonitorService(getApplicationContext());
            monitor = monitorService.findAutenticado();
            monitorado.setId(it.getExtras().getLong("idMonitorado"));
            mensagem.setMonitor(monitor);
            mensagem.setData("");
            mensagem.setMonitorado(monitorado);
            mensagem.setTipo(1);
            idMensagem = mensagemService.save(mensagem);
            refreshMensagems();
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpUtil.post(JsonUtil.toJson(mensagem), CollabtrackApplication.API_PATH_MENSAGEM, new HttpCallback() {

                @Override
                public void onSuccess(String response) {
                    MensagemService mensagemService = new MensagemService(getApplicationContext());
                    MensagemTO mensagemRetorno = (MensagemTO) JsonUtil.toObject(response, MensagemTO.class);
                    Mensagem mensagemEdit = new Mensagem();
                    mensagemEdit.setId(mensagemRetorno.getId());
                    mensagemEdit.setData(mensagemRetorno.getData().toString());
                    mensagemService.updateId(mensagemEdit, idMensagem);
                    refreshMensagems();
                }

                @Override
                public void onError(VolleyError error) {
                }
            });

            return null;
        }
    }

    private void refreshMensagems(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            MensagemSQL mensagemSQL = new MensagemSQL(getBaseContext());
            this.lista = mensagemSQL.buscaPorMonitorado(getIntent().getExtras().getLong("idMonitorado"), principal);

            MonitorService monitorService = new MonitorService(getBaseContext());
            Monitor monitor = monitorService.findAutenticado();

            ltwChats.setAdapter(new ChatAdapter(ChatActivity.this, lista, monitor.getId(), getIntent().getExtras().getLong("idMonitorado"), monitores));
            ltwChats.setSelection(ltwChats.getCount() - 1);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH_CHAT");
        intentFilter.addAction(CollabtrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.hasExtra("acao") && intent.getIntExtra("acao", 0) == DataFirebaseTO.ACAO_REMOCAO_MONITORADO && intent.hasExtra("id_monitorado")){

                if (intent.getLongExtra("id_monitorado", 0) == idMonitorado) {
                    hideKeyboard();
                    finish();
                }

            } else {
                refreshMensagems();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
            case R.id.nav_mensagem_excluir:
                startDialogConfirmacao();
                break;
        }
        return false;
    }

    public List<Monitor> buscaMonitoresParaChat(long idMonitor, long idMonitorado){
        new BuscarMonitorRequest(idMonitor, idMonitorado, new HttpCallback() {
            @Override
            public void onSuccess(String response) {
                // Instância do monitor e da classe de servço

                if(response == null || response.equals("null")){
                   monitores = JsonUtil.toObjectList(response, Monitor[].class);
                }
                refreshMensagems();
            }

            @Override
            public void onError(VolleyError error) {

            }
        }).execute();
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        refreshMensagems();
                    }else{
                        finish();
                    }
                } else {
                }
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                        refreshMensagems();
                    }else{
                        finish();
                    }
                } else {
                    finish();
                }
            }
        }
    }

    private void startDialogConfirmacao(){

        // Bundle
        Bundle bundle = new Bundle();

        // Mensagem a ser excluida
        bundle.putInt("idMonitorado", (int) getIntent().getExtras().getLong("idMonitorado"));

        FragmentManager fm = getSupportFragmentManager();
        // Carrega o dialog
        MensagemDialogExcluirFragment mensagemDialogExcluirFragment = new MensagemDialogExcluirFragment();
        mensagemDialogExcluirFragment.setArguments(bundle);
        mensagemDialogExcluirFragment.show(fm, CollabtrackApplication.DEBUG_TAG);

    }
}
