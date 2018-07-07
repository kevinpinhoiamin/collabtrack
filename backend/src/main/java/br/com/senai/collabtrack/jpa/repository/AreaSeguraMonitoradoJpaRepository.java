package br.com.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.com.senai.collabtrack.entity.AreaSeguraMonitoradoPK;

public interface AreaSeguraMonitoradoJpaRepository extends JpaRepository<AreaSeguraMonitorado, Long> {

	AreaSeguraMonitorado findFistByPrimaryKey(AreaSeguraMonitoradoPK primaryKey);
	
}
