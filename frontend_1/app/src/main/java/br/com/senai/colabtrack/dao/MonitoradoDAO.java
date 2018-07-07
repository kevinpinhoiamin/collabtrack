package br.com.senai.colabtrack.dao;

import java.util.List;

import br.com.senai.colabtrack.domain.MonitorMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;

/**
 * Created by kevin on 22/06/17.
 */

public interface MonitoradoDAO extends GenericDAO<Monitorado,Long> {
    List<Monitorado> findActives();
    List<Monitorado> findPrincipal();
    int deleteByMonitorado(Long idMonitorado);
}
