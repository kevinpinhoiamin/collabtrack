package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.Monitor;

public interface MonitorRepository {
	List<Monitor> findAtivoAndEmMonitoramentoByMonitoradoId(long idMonitorado);

	List<Monitor> findOthersRelated(long idMonitor, long idMonitorado);

	Monitor findPrincipalByMonitoradoCelular(long celular);
	Monitor buscaPrincipalByCelularMonitorado(long celular);
	Monitor findByMonitoradoCelularAndTokenAutenticacao(long celularMonitorado, String tokenAutenticacao);
}
