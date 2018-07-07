package br.com.senai.colabtrack.service;

import android.content.Context;

import java.util.List;
import java.util.ListIterator;

import br.com.senai.colabtrack.dao.AreaSeguraMonitoradoDAO;
import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.sql.AreaSeguraMonitoradoSQL;

/**
 * Created by kevin on 22/06/17.
 */

public class AreaSeguraMonitoradoService {

    Context context;
    AreaSeguraMonitoradoDAO areaSeguraMonitoradoDAO;

    public AreaSeguraMonitoradoService(Context context){
        this.context = context;
        areaSeguraMonitoradoDAO = new AreaSeguraMonitoradoSQL(this.context);
    }

    public Long save(AreaSeguraMonitorado areaSeguraMonitorado){
        return areaSeguraMonitoradoDAO.save(areaSeguraMonitorado);
    }

    public int delete(Long idAreaSegura){
        return areaSeguraMonitoradoDAO.delete(idAreaSegura);
    }

    public Long update(AreaSeguraMonitorado areaSeguraMonitorado){
        return areaSeguraMonitoradoDAO.update(areaSeguraMonitorado);
    }

    private List<AreaSeguraMonitorado> findAreaSeguraMonitorado(List<AreaSeguraMonitorado> areaSeguraMonitorados){

        if(areaSeguraMonitorados != null && areaSeguraMonitorados.size() > 0){
            // Classe de serviço
            AreaSeguraService areaSeguraService = new AreaSeguraService(this.context);
            MonitoradoService monitoradoService = new MonitoradoService(this.context);

            // Iterator
            ListIterator iterator = areaSeguraMonitorados.listIterator();
            while (iterator.hasNext()){
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Pegando o item da lista
                AreaSeguraMonitorado areaSeguraMonitorado = (AreaSeguraMonitorado) iterator.next();
                // Setando a área segura e o monitorado
                areaSeguraMonitorado.setAreaSegura(areaSeguraService.find(areaSeguraMonitorado.getAreaSegura().getId()));
                areaSeguraMonitorado.setMonitorado(monitoradoService.find(areaSeguraMonitorado.getMonitorado().getId()));
                // Substituindo o item da lista
                areaSeguraMonitorados.set(position, areaSeguraMonitorado);
            }
        }

        return areaSeguraMonitorados;

    }

    public List<AreaSeguraMonitorado> findPrincipalByAreaSegura(Long idAreaSegura){
        return findAreaSeguraMonitorado(areaSeguraMonitoradoDAO.findPrincipalByAreaSegura(idAreaSegura));
    }

    public List<AreaSeguraMonitorado> findByAreaSegura(Long idAreaSegura){
        return findAreaSeguraMonitorado(areaSeguraMonitoradoDAO.findByAreaSegura(idAreaSegura));
    }

    public int deletarAssociacoes(Long idMonitorado){
        return areaSeguraMonitoradoDAO.deleteByMonitorado(idMonitorado);
    }

}
