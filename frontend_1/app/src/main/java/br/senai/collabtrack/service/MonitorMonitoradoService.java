package br.senai.collabtrack.service;

import android.content.Context;

import java.util.List;

import br.senai.collabtrack.dao.MonitorMonitoradoDAO;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.sql.MonitorMonitoradoSQL;

/**
 * Created by kevin on 23/06/17.
 */

public class MonitorMonitoradoService {

    private Context context;
    private MonitorMonitoradoDAO monitorMonitoradoDAO;

    public MonitorMonitoradoService(Context context){
        this.context = context;
        this.monitorMonitoradoDAO = new MonitorMonitoradoSQL(context);
    }

    public Long save(MonitorMonitorado monitorMonitorado){
        return monitorMonitoradoDAO.save(monitorMonitorado);
    }

    public List<MonitorMonitorado> findPrincipalByMonitor(Long monitorID){
        return monitorMonitoradoDAO.findPrincipalByMonitor(monitorID);
    }

    public List<MonitorMonitorado> findAll(){
        return monitorMonitoradoDAO.findAll();
    }

    public MonitorMonitorado find(long idMonitor, long idMonitorado){
        return monitorMonitoradoDAO.find(idMonitor, idMonitorado);
    }

    public Long update(MonitorMonitorado monitorMonitorado){
        return monitorMonitoradoDAO.update(monitorMonitorado);
    }

    public int delete(long idMonitor, long idMonitorado){
        return monitorMonitoradoDAO.delete(idMonitor, idMonitorado);
    }

    public int deletarAssociacoes(Long idMonitorado){
        return monitorMonitoradoDAO.deleteByMonitorado(idMonitorado);
    }

}
