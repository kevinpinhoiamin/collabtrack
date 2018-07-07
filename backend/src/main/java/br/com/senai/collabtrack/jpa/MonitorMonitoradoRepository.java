package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.MonitorMonitorado;

public interface MonitorMonitoradoRepository {

	List<MonitorMonitorado> findByMonitorId(long idMonitor);

	List<MonitorMonitorado> findByMonitoradoId(long idMonitorado);

	MonitorMonitorado findPrincipalByMonitoradoId(long idMonitorado);

	boolean isEmMonitoramento(long idMonitorado);

}
