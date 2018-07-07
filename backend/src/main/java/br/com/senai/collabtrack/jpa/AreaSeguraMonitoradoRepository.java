package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.AreaSeguraMonitorado;

public interface AreaSeguraMonitoradoRepository{
	List<AreaSeguraMonitorado> findByIdAndCelular(long id, long celular);
	List<AreaSeguraMonitorado> findByIdMonitorado(long idMonitorado);
}
