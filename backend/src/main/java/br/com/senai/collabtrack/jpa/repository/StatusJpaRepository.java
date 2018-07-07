package br.com.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.collabtrack.entity.Monitorado;
import br.com.senai.collabtrack.entity.Status;

public interface StatusJpaRepository extends JpaRepository<Status, Long>{
	Status findFirstByMonitorado(Monitorado monitorado);
}
