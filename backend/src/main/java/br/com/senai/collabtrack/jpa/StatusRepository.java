package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.Status;

public interface StatusRepository {

	public List<Status> findByMonitorCelular(long celular);
	
}
