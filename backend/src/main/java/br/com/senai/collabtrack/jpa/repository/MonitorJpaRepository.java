package br.com.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.collabtrack.entity.Monitor;

public interface MonitorJpaRepository extends JpaRepository<Monitor, Long> {

	Monitor findFirstByCelular(long celular);
	Monitor findFirstByCelularAndTokenAutenticacao(long celular, String tokenAutenticacao);
	
}
