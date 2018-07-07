package br.com.senai.colabtrack.service;

import android.content.Context;

import br.com.senai.colabtrack.dao.MonitorDAO;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.sql.MonitorSQL;

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
