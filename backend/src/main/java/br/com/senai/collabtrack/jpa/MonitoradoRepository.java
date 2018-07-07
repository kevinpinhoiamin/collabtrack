package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.Monitorado;

public interface MonitoradoRepository {

	boolean isEmMonitoramento(long idMonitorado);
	List<Monitorado> findByMonitorIdPrincipal(long idMonitor);
	
}
