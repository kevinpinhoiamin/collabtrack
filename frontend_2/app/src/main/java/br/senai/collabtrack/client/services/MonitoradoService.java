package br.senai.collabtrack.client.services;

import android.content.Context;
import android.widget.ImageButton;

import com.android.volley.VolleyError;
import com.squareup.picasso.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import br.senai.collabtrack.client.object.AppException;
import br.senai.collabtrack.client.object.Monitorado;
import br.senai.collabtrack.client.sql.MonitoradoDAO;
import br.senai.collabtrack.client.Application;
import br.senai.collabtrack.client.util.HttpCallback;
import br.senai.collabtrack.client.util.HttpUtil;
import br.senai.collabtrack.client.util.Server;

/**
 * Created by ezs on 18/08/2017.
 */

public class MonitoradoService {

    public static final String REST_PHOTO            = Application.SERVER_URL + "/monitor/picture/";
    public static final String REST_MONITORADO       = Application.SERVER_URL + "/monitorado/";
    public static final String REST_EM_MONITORAMENTO = Application.SERVER_URL + "/monitorado/em-monitoramento/";
    public static final String REST_MONITORADO_TOKEN = Application.SERVER_URL + "/monitorado/token/";
    public static final String REST_MONITOR          = Application.SERVER_URL + "/monitor/monitorado/";

    private static Monitorado monitorado = null;
    private MonitoradoDAO monitoradoDAO = null;

    public MonitoradoService() {
        monitoradoDAO = new MonitoradoDAO(Application.getContext());
    }

    public MonitoradoService(Context context){
        monitoradoDAO = new MonitoradoDAO(context);
    }

    public static Monitorado getThisMonitorado() {
        return getThisMonitorado(Application.getContext());
    }

    public static Monitorado getThisMonitorado(Context context){
        return monitorado != null ? monitorado : (monitorado = new MonitoradoDAO(context).find());
    }

    public boolean emMonitoramento(){

        boolean val = emMonitoramento0();
        Application.log("Monitoramento -> " + val);
        return val;
    }

    private boolean emMonitoramento0(){
        //Sempre dar preferência por iniciar o monitoramento, seja ocorrendo erro ou o retorno do servidor vir errado.
        try {
            Monitorado monitorado = MonitoradoService.getThisMonitorado();
            String resp = Server.get(REST_EM_MONITORAMENTO + monitorado.getId());
            if (resp != null && !resp.isEmpty()) {
                return !resp.toLowerCase().equals("false");
            } else
                Application.log("else");
        } catch (AppException e){
            Application.log("Falha ao verificar se o monitorado está com monitoramento ativo", e);
        }
        //Se ocorrer falha ao verificar se está em monitoramento, inicia o serviço.
        return true;
    }

    public void refreshToken(String token) {
        try {
            Application.log("refresh token a enviar");
            String url = REST_MONITORADO_TOKEN + getThisMonitorado().getId() + "/" + token;
            Application.log(url);
            HttpUtil.put("", url, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    Application.log("ok ao enviar o token");
                }
                @Override
                public void onError(VolleyError error) {
                    Application.log("error ao enviar o token");
                }
            });
        } catch (Exception e) {
            Application.log(e.getMessage());
        }
    }

    public void loadPhotoMonitorado(ImageButton imageButton) {
        String url = REST_PHOTO + getThisMonitorado().getCelularMonitor();
        HttpUtil.getImage(url,
                imageButton,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Application.log("Sucesso");
                    }

                    @Override
                    public void onError() {
                        Application.log("erro");
                    }

                }, true);
    }

    public InputStream getPhotoMonitorado(boolean disabled) {
        String url = REST_PHOTO + getThisMonitorado().getId()  + ".jpg";
        Application.log("download photo from: " + url);
        try {
            return ((new URL(url).openConnection()).getInputStream());
        } catch (IOException e) {
            Application.log("Falha ao fazer o download da imagem");
            Application.log(e);
            return null;
        }
    }

    public void deleteAll(){
        monitoradoDAO.deleteAll();
    }

    public void save(Monitorado monitorado){
        monitoradoDAO.save(monitorado);
    }

}
