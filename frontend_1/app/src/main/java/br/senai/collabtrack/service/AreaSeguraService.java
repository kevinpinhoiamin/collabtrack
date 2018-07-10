package br.senai.collabtrack.service;

import android.content.Context;
import android.util.Log;

import java.util.List;

import br.senai.collabtrack.dao.AreaSeguraDAO;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.sql.AreaSeguraSQL;

/**
 * Created by kevin on 31/05/17.
 */

public class AreaSeguraService{

    Context context;
    AreaSeguraDAO areaSeguraDAO;

    public AreaSeguraService(Context context){
        this.context = context;
        this.areaSeguraDAO = new AreaSeguraSQL(this.context);
    }

    public Long save(AreaSegura areaSegura) {
        return areaSeguraDAO.save(areaSegura);
    }

    public Long update(AreaSegura areaSegura){
        return areaSeguraDAO.update(areaSegura);
    }

    public int delete(Long id) {
        return areaSeguraDAO.delete(id);
    }

    public List<AreaSegura> findAll() {
        return areaSeguraDAO.findAll();
    }

    public List<AreaSegura> findByMonitorado(Long idMonitorado){
        return areaSeguraDAO.findByMonitorado(idMonitorado);
    }

    public List<AreaSegura> findAtivasByMonitorado(Long idMonitorado) {
        return areaSeguraDAO.findAtivasByMonitorado(idMonitorado);
    }

    public List<AreaSegura> search(String search) {
        return areaSeguraDAO.search(search);
    }

    public AreaSegura find(Long id){
        return areaSeguraDAO.find(id);
    }

}
