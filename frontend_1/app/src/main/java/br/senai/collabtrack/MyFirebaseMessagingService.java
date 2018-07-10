package br.senai.collabtrack;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.senai.collabtrack.domain.Localizacao;
import br.senai.collabtrack.domain.Mensagem;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;
import br.senai.collabtrack.domain.to.DataFirebaseTO;
import br.senai.collabtrack.domain.to.MonitoradoTO;
import br.senai.collabtrack.service.MensagemService;
import br.senai.collabtrack.service.MonitoradoService;
import br.senai.collabtrack.service.StatusService;
import br.senai.collabtrack.service.mapa.MapAreaService;
import br.senai.collabtrack.util.JsonUtil;
import br.senai.collabtrack.util.NotificationUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            DataFirebaseTO dataFirebaseTO = new DataFirebaseTO();
            dataFirebaseTO.setAcao(Integer.parseInt(remoteMessage.getData().get("acao") != null ? remoteMessage.getData().get("acao") : ""));
            dataFirebaseTO.setMensagem(remoteMessage.getData().get("mensagem") != null ? remoteMessage.getData().get("mensagem").toString() : "");
            dataFirebaseTO.setId(Long.parseLong(remoteMessage.getData().get("id") != null ? remoteMessage.getData().get("id") : ""));
            dataFirebaseTO.setJson(remoteMessage.getData().get("json") != null ? String.valueOf(remoteMessage.getData().get("json")) : "");

            switch (dataFirebaseTO.getAcao()){
                case DataFirebaseTO.ACAO_AREA_SEGURA:
                    Log.e(CollabtrackApplication.DEBUG_TAG, dataFirebaseTO.getJson());
                    MapAreaService.cadastrarLocalizacao((Localizacao) JsonUtil.toObject(dataFirebaseTO.getJson(), Localizacao.class));
                    break;
                case DataFirebaseTO.ACAO_ATUALIZAR_STATUS:
                    Log.e(CollabtrackApplication.DEBUG_TAG, dataFirebaseTO.getJson());
                    StatusService.cadastrarOuAlterar((Status)JsonUtil.toObject(dataFirebaseTO.getJson(), Status.class), dataFirebaseTO.getMensagem());
                    break;
                case DataFirebaseTO.ACAO_CADASTRO_MONITORADO:
                    MonitoradoService.save((MonitoradoTO) JsonUtil.toObject(dataFirebaseTO.getJson(), MonitoradoTO.class));
                    break;
                case DataFirebaseTO.ACAO_EDICAO_MONITORADO:
                    MonitoradoService.update((MonitoradoTO) JsonUtil.toObject(dataFirebaseTO.getJson(), MonitoradoTO.class), dataFirebaseTO.getId());
                    break;
                case DataFirebaseTO.ACAO_REMOCAO_MONITORADO:
                    MonitoradoService.delete(dataFirebaseTO.getId());
                    break;
                case DataFirebaseTO.ACAO_DIVERSOS:
                    JSONObject jsonObjDiv = null;
                    NotificationUtil.showNotification(getString(R.string.aviso), "Novo aviso");
                    try {
                        jsonObjDiv = new JSONObject(remoteMessage.getData().get("objeto"));
                        JSONObject jsonObjMonitor = (JSONObject) jsonObjDiv.get("monitor");
                        JSONObject jsonObjMonitorado = (JSONObject) jsonObjDiv.get("monitorado");

                        Monitor monitor = new Monitor();
                        monitor.setId(Integer.parseInt(String.valueOf(jsonObjMonitor.get("id"))));

                        Monitorado monitorado = new Monitorado();
                        monitorado.setId(Integer.parseInt(String.valueOf(jsonObjMonitorado.get("id"))));

                        Mensagem mensagem = new Mensagem();
                        mensagem.setId(Integer.parseInt(String.valueOf(jsonObjDiv.get("id"))));
                        mensagem.setMensagem(jsonObjDiv.get("mensagem").toString());
                        mensagem.setData(jsonObjDiv.get("data").toString());
                        mensagem.setResposta((Integer) jsonObjDiv.get("resposta"));
                        mensagem.setMonitor(monitor);
                        mensagem.setTipo((Integer) jsonObjDiv.get("tipo"));
                        mensagem.setMonitorado(monitorado);

                        MensagemService mensagemService = new MensagemService(getBaseContext());
                        mensagemService.save(mensagem);

                        Intent it = new Intent("REFRESH_CHAT");
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(it);
                        //MensagemService.inserirMensagem(jsonObjMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DataFirebaseTO.ACAO_RESPOSTA:
                    JSONObject jsonObjRes = null;
                    NotificationUtil.showNotification(getString(R.string.mensagem), "Áudio respondido");
                    try {
                        jsonObjRes = new JSONObject(remoteMessage.getData().get("objeto"));
                        JSONObject jsonObjMonitor = (JSONObject) jsonObjRes.get("monitor");
                        JSONObject jsonObjMonitorado = (JSONObject) jsonObjRes.get("monitorado");

                        Monitor monitor = new Monitor();
                        monitor.setId(Integer.parseInt(String.valueOf(jsonObjMonitor.get("id"))));

                        Monitorado monitorado = new Monitorado();
                        monitorado.setId(Integer.parseInt(String.valueOf(jsonObjMonitorado.get("id"))));

                        Mensagem mensagem = new Mensagem();
                        mensagem.setId(Integer.parseInt(String.valueOf(jsonObjRes.get("id"))));
                        mensagem.setMensagem(jsonObjRes.get("mensagem").toString());
                        mensagem.setData(jsonObjRes.get("data").toString());
                        mensagem.setResposta((Integer) jsonObjRes.get("resposta"));
                        mensagem.setMonitor(monitor);
                        mensagem.setTipo(3);
                        mensagem.setMonitorado(monitorado);

                        MensagemService mensagemService = new MensagemService(getBaseContext());
                        mensagemService.save(mensagem);

                        Intent it = new Intent("REFRESH_CHAT");
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(it);
                        //MensagemService.inserirMensagem(jsonObjMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DataFirebaseTO.ACAO_MENSAGEM:
                    JSONObject jsonObjMsg = null;
                    try {
                        jsonObjMsg = new JSONObject(remoteMessage.getData().get("objeto"));
                        JSONObject jsonObjMonitor = (JSONObject) jsonObjMsg.get("monitor");
                        JSONObject jsonObjMonitorado = (JSONObject) jsonObjMsg.get("monitorado");

                        Monitor monitor = new Monitor();
                        monitor.setId(Integer.parseInt(String.valueOf(jsonObjMonitor.get("id"))));

                        Monitorado monitorado = new Monitorado();
                        monitorado.setId(Integer.parseInt(String.valueOf(jsonObjMonitorado.get("id"))));

                        Mensagem mensagem = new Mensagem();
                        mensagem.setId(Integer.parseInt(String.valueOf(jsonObjMsg.get("id"))));
                        mensagem.setMensagem(jsonObjMsg.get("mensagem").toString());
                        mensagem.setData(jsonObjMsg.get("data").toString());
                        mensagem.setResposta((Integer) jsonObjMsg.get("resposta"));
                        mensagem.setMonitor(monitor);
                        mensagem.setTipo(1);
                        mensagem.setMonitorado(monitorado);

                        MensagemService mensagemService = new MensagemService(getBaseContext());
                        mensagemService.save(mensagem);

                        NotificationUtil.showNotification(getString(R.string.mensagem), jsonObjMsg.get("mensagem").toString());

                        Intent it = new Intent("REFRESH_CHAT");
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(it);
                        //MensagemService.inserirMensagem(jsonObjMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }

    }

}