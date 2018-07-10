package br.senai.collabtrack.service;

import android.content.Context;

import br.senai.collabtrack.dao.MonitorDAO;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.sql.MonitorSQL;

/**
 * Created by kevin on 13/06/17.
 */

public class MonitorService{

    private Context context;
    private MonitorDAO monitorDAO;

    public MonitorService(Context context){
        this.context = context;
        this.monitorDAO = new MonitorSQL(this.context);
    }

    public Monitor find(Long id){
        return monitorDAO.find(id);
    }

    public Monitor findAutenticado(){
        return monitorDAO.findAutenticado();
    }

    public Long save(Monitor monitor){
        return monitorDAO.save(monitor);
    }

    public Long update(Monitor monitor){
        return monitorDAO.update(monitor);
    }

}
