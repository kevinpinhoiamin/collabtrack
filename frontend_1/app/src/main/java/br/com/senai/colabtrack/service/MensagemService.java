package br.com.senai.colabtrack.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.senai.colabtrack.dao.MensagemDAO;
import br.com.senai.colabtrack.domain.Mensagem;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.sql.MensagemSQL;

/**
 * Created by Lucas Matheus Nunes on 24/08/2017.
 */

public class MensagemService {
    private static Context context;
    private MensagemDAO mensagemDAO;

    public MensagemService(Context context){
        this.context = context;
        this.mensagemDAO = new MensagemSQL(context);
    }

    public Long save(Mensagem mensagem){
        return mensagemDAO.save(mensagem);
    }

    public void updadeMensagem(long id, String data){
        mensagemDAO.updadeMensagem(id, data);
    }
    public void updateId(Mensagem mensagem, Long id){
        mensagemDAO.updateId(mensagem, id);
    }

    public static void inserirMensagem(JSONObject jsonObjMsg) throws JSONException {
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
        mensagem.setMonitorado(monitorado);

        MensagemService mensagemService = new MensagemService(context);

        Log.d("Salvou: ", "Monitor: " + mensagem.getMonitor().getId()+" Monitorado: "+mensagem.getMonitorado().getId()+" Mensagem: "+mensagem.getMensagem());

        mensagemService.save(mensagem);

        Intent it = new Intent("REFRESH_CHAT");
        LocalBroadcastManager.getInstance(context).sendBroadcast(it);
    }

    public int deletarMensamgens(Long idMonitorado){
        return mensagemDAO.deleteByMonitorado(idMonitorado);
    }

    public Mensagem findUltimaMensagem(Long idMonitorado) {
        return this.mensagemDAO.findUltimaMensagem(idMonitorado);
    }

}
