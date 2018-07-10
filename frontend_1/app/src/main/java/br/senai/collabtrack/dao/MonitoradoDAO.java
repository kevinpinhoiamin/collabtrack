package br.senai.collabtrack.dao;

import java.util.List;

import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;

/**
 * Created by kevin on 22/06/17.
 */

public interface MonitoradoDAO extends GenericDAO<Monitorado,Long> {
    List<Monitorado> findActives();
    List<Monitorado> findPrincipal();
    int deleteByMonitorado(Long idMonitorado);
}
