package br.com.senai.colabtrack.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.ListIterator;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.dao.StatusDAO;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.Status;
import br.com.senai.colabtrack.domain.to.DataFirebaseTO;
import br.com.senai.colabtrack.domain.util.StatusUtil;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.sql.StatusSQL;
import br.com.senai.colabtrack.util.DateUtil;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;

/**
 * Created by kevin on 8/8/17.
 */

public class StatusService {

    private Context context;
    private StatusDAO statusDAO;

    public StatusService(Context context){
        this.context = context;
        this.statusDAO = new StatusSQL(this.context);
    }

    private Long save(Status status){
        return statusDAO.save(status);
    }

    private Long update(Status status){
        return statusDAO.update(status);
    }

    public Long saveOrUpdate(Status status){

        Status statusDB = this.findByMonitorado(status.getMonitorado().getId());
        if(statusDB == null){
            return this.save(status);
        }else{
            statusDB.setBateria(status.getBateria());
            statusDB.setData(status.getData());
            statusDB.setLocalizacao(status.isLocalizacao());
            statusDB.setInternetMovel(status.isInternetMovel());
            statusDB.setWifi(status.isWifi());
            return this.update(statusDB);
        }

    }

    public Status findByMonitorado(Long idMonitorado){
        return statusDAO.findByMonitorado(idMonitorado);
    }

    public List<Status> findAll(){
        return statusDAO.findAll();
    }

    public static void cadastrarOuAlterar(Status status, String data){

        StatusService statusService = new StatusService(ColabTrackApplication.getContext());
        status.setData(data);
        if(status.getId() == 0) {
            status.setId(statusService.saveOrUpdate(status));
        } else {
            statusService.saveOrUpdate(status);
        }

        // Dispara o broadcast para a lista de status
        Intent intent = new Intent(ColabTrackApplication.STATUS_BROADCAST_RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putParcelable("status", StatusUtil.writeToParcel(status));
        intent.putExtras(bundle);
        ColabTrackApplication.getContext().sendBroadcast(intent);

    }

    public int deletarStatus(Long idMonitorado){
        return statusDAO.deleteByMonitorado(idMonitorado);
    }

}
