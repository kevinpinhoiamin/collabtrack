package br.senai.collabtrack.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.domain.AreaSeguraMonitorado;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.service.AreaSeguraMonitoradoService;
import br.senai.collabtrack.service.AreaSeguraService;
import br.senai.collabtrack.service.MonitorMonitoradoService;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.service.MonitoradoService;
import br.senai.collabtrack.service.StatusService;
import br.senai.collabtrack.util.HttpUtil;
import br.senai.collabtrack.util.JsonUtil;

/**
 * Created by kevin on 9/26/17.
 */

public class SincronizarDadosRequest extends AsyncTask<Void, Void, Void>{

    private boolean sincronizacaAreaSeguraMonitoradoCompleta = false;
    private boolean sincronizacaoMonitorMonitorado = false;
    private boolean sincronizacaoStatus = false;

    public interface FinalizarSincronizacao{
        void onFinalizarSincronizacao(boolean sincronizacaAreaSeguraMonitoradoCompleta,
                                      boolean sincronizacaoMonitorMonitorado,
                                      boolean sincronizacaoStatus);
        void onError();
    }

    private long celular;
    private long idMonitor;
    FinalizarSincronizacao finalizarSincronizacao;
    public SincronizarDadosRequest(long celular, long idMonitor, FinalizarSincronizacao finalizarSincronizacao){
        this.celular = celular;
        this.idMonitor = idMonitor;
        this.finalizarSincronizacao = finalizarSincronizacao;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Requisição HTTP para buscar a relação entre áreas seguras e monitorados
        HttpUtil.get(CollabtrackApplication.API_PATH_AREA_SEGURA_MONITORADO+"?celular="+celular, new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                // Serviços
                AreaSeguraService areaSeguraService = new AreaSeguraService(CollabtrackApplication.getContext());
                AreaSeguraMonitoradoService areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(CollabtrackApplication.getContext());

                // Convertendo a lista de JSON para Object
                List<AreaSeguraMonitorado> areaSeguraMonitorados = JsonUtil.toObjectList(response, AreaSeguraMonitorado[].class);

                // Lista com os objetos a serem removidos
                List<AreaSegura> areaSegurasRemovidas = new ArrayList<>();

                // Iteração para efetuar os cadastros
                for (AreaSeguraMonitorado areaSeguraMonitorado : areaSeguraMonitorados){

                    // Área segura
                    AreaSegura areaSegura = areaSeguraMonitorado.getPrimaryKey().getAreaSegura();
                    boolean cadastrarAreaSegura = true;
                    // Iteração para verificar se a área segura já foi cadastrada
                    for (AreaSegura areaSeguraRemovida : areaSegurasRemovidas){
                        if(areaSeguraRemovida.getId() == areaSegura.getId()){
                            cadastrarAreaSegura = false;
                        }
                    }
                    if(cadastrarAreaSegura){
                        areaSeguraService.save(areaSegura);
                        areaSegurasRemovidas.add(areaSegura);
                    }

                    // Relação entre área segura e monitorado
                    areaSeguraMonitoradoService.save(areaSeguraMonitorado);

                }

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

                // Carrega o mapa
                sincronizacaAreaSeguraMonitoradoCompleta = true;
                finalizarSincronizacao.onFinalizarSincronizacao(sincronizacaAreaSeguraMonitoradoCompleta,
                                                                sincronizacaoMonitorMonitorado,
                                                                sincronizacaoStatus);

            }

            @Override
            public void onError(VolleyError error) {
                finalizarSincronizacao.onError();
            }
        });

        // Requisição HTTP para buscar a relação entre monitor e monitorado
        HttpUtil.get(CollabtrackApplication.API_PATH_MONITOR_MONITORADO+"?id-monitor="+idMonitor, new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                // Serviços
                MonitorMonitoradoService monitorMonitoradoService = new MonitorMonitoradoService(CollabtrackApplication.getContext());
                MonitoradoService monitoradoService = new MonitoradoService(CollabtrackApplication.getContext());

                // Convertendo a lista de JSON para Object
                List<MonitorMonitorado> monitorMonitorados = JsonUtil.toObjectList(response, MonitorMonitorado[].class);

                // Lista com os objetos a serem removidos
                List<Monitorado> monitoradosRemovidos = new ArrayList<>();

                // Iteração para efetuar os cadastros
                for(MonitorMonitorado monitorMonitorado : monitorMonitorados){
                    // Relação entre monitor e monitorado
                    monitorMonitoradoService.save(monitorMonitorado);

                    // Monitorado
                    Monitorado monitorado = monitorMonitorado.getPrimaryKey().getMonitorado();
                    boolean cadastrarMonitorado = true;
                    // Iteração para verificar se o monitorado já foi cadastrado
                    for (Monitorado monitoradoRemovido : monitoradosRemovidos){
                        if(monitoradoRemovido.getId() == monitorado.getId()){
                            cadastrarMonitorado = false;
                        }
                    }
                    if(cadastrarMonitorado){
                        monitoradoService.save(monitorado);
                        monitoradosRemovidos.add(monitorado);
                    }

                }

                // Carrega o mapa
                sincronizacaoMonitorMonitorado = true;
                finalizarSincronizacao.onFinalizarSincronizacao(sincronizacaAreaSeguraMonitoradoCompleta,
                                                                sincronizacaoMonitorMonitorado,
                                                                sincronizacaoStatus);

            }

            @Override
            public void onError(VolleyError error) {
                finalizarSincronizacao.onError();
            }
        });

        /*// Requisição HTTP para buscar as mensagens dos monitorados relacionado ao monitor
        HttpUtil.get(CollabtrackApplication.API_PATH_MENSAGEM+"?celular="+celular, new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                //Serviços
                MensagemService mensagemService = new MensagemService(CollabtrackApplication.getContext());

                // Convertendo a lista de JSON para Object
                List<Mensagem> mensagens = JsonUtil.toObjectList(response, Mensagem[].class);

                // Iteração para efetuar os cadastros
                for(Mensagem mensagem : mensagens){
                    mensagemService.save(mensagem);
                }

            }

            @Override
            public void onError(VolleyError error) {
                finalizarSincronizacao.onError();
            }
        });*/

        // Requisição HTTP para buscar os status dos monitorados
        HttpUtil.get(CollabtrackApplication.API_PATH_STATUS+"?celular="+celular, new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                // Serviços
                StatusService statusService = new StatusService(CollabtrackApplication.getContext());

                // Convertendo a lista de JSON para Object
                List<br.senai.collabtrack.domain.Status> statusList = JsonUtil.toObjectList(response, br.senai.collabtrack.domain.Status[].class);

                // Status
                for(br.senai.collabtrack.domain.Status status : statusList) {
                    statusService.saveOrUpdate(status);
                }

                // Carrega o mapa
                sincronizacaoStatus = true;
                finalizarSincronizacao.onFinalizarSincronizacao(sincronizacaAreaSeguraMonitoradoCompleta,
                                                                sincronizacaoMonitorMonitorado,
                                                                sincronizacaoStatus);

            }

            @Override
            public void onError(VolleyError error) {
                finalizarSincronizacao.onError();
            }
        });

        return null;

    }
}
