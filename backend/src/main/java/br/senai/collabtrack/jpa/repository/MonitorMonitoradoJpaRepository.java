package br.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.senai.collabtrack.entity.MonitorMonitorado;
import br.senai.collabtrack.entity.MonitorMonitoradoPK;

public interface MonitorMonitoradoJpaRepository extends JpaRepository<MonitorMonitorado, Long> {
	MonitorMonitorado findFirstByPrimaryKey(MonitorMonitoradoPK primaryKey);
}
