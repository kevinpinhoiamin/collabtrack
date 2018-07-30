package br.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.senai.collabtrack.entity.Monitorado;

public interface MonitoradoJpaRepository extends JpaRepository<Monitorado, Long> {
	Monitorado findFirstByCelular(long celular);
}
