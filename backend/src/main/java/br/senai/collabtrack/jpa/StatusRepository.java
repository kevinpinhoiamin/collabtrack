package br.senai.collabtrack.jpa;

import java.util.List;

import br.senai.collabtrack.entity.Status;

public interface StatusRepository {

	public List<Status> findByMonitorCelular(long celular);
	
}
