package br.com.senai.colabtrack.dao;

import java.util.List;

import br.com.senai.colabtrack.domain.MonitorMonitorado;

/**
 * Created by kevin on 23/06/17.
 */

public interface MonitorMonitoradoDAO extends GenericDAO<MonitorMonitorado, Long>{
    int delete(Long id_monitor, Long id_monitorado);
    MonitorMonitorado find(Long monitorID, Long monitoradoID);
    List<MonitorMonitorado> findPrincipalByMonitor(Long monitorID);
    int deleteByMonitorado(Long idMonitorado);
}
