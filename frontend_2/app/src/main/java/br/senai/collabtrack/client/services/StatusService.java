package br.senai.collabtrack.client.services;

import android.content.Context;

import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.broadcast.NetworkStateReceiver;
import br.senai.collabtrack.client.object.Status;
import br.senai.collabtrack.client.util.HttpUtil;

/**
 * Created by ezs on 17/08/2017.
 */

public class StatusService {

    private static Status status = null;

    public StatusService(){

    }

    public StatusService(Context context){

    }

    public static Status getStatus() {
        return (status != null ? status : (status = new Status()));
    }

    public static void atualizar(){
        determinarAtualizacao();
    }

    public static  void determinarAtualizacao(){
        determinarAtualizacao(Application.getContext());
    }

    public static void determinarAtualizacao(Context context){
        Application.log("determinarAtualizacao...............................................................");
        if (internetConectada()) {
            if (getStatus().getBateria() != 0 || new MonitoradoService(context).emMonitoramento()) {
                Application.log(getStatus().toString());
                if (getStatus().getMonitorado() == null) {
                    Application.log("monitorado == null");
                    getStatus().setMonitorado(MonitoradoService.getThisMonitorado());
                    Application.log(getStatus().toString());
                }
                if (getStatus().getBateria() == 0){
                    Application.log("Bateria = 0 -> não envia para o servidor");
                } else {
                    HttpUtil.post(getStatus(), Application.SERVER_URL + "/status", false, null);
                }
            } else {
                Application.log("Não atualizou status pois não foi inicializado o serviço pelo monitor");
            }
        } else {
            Application.log("Não atualizou status pois não há conexão com internet");
        }
    }

    public static boolean internetConectada() {
        Application.log("internetConectada ");
        Application.log("isInternetMovel " + getStatus().isInternetMovel());
        Application.log("isWifi " + getStatus().isWifi());
        return conexaoValida();
    }

    private static boolean conexaoValida() {
        if (getStatus().isInternetMovel() || getStatus().isWifi()) {
            return true;
        } else {
            new NetworkStateReceiver().refreshConnectivityStatus(Application.getContext());
            return getStatus().isInternetMovel() || getStatus().isWifi();
        }
    }
}
