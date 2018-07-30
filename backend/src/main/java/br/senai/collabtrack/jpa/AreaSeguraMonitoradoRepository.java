package br.senai.collabtrack.jpa;

import java.util.List;

import br.senai.collabtrack.entity.AreaSeguraMonitorado;

public interface AreaSeguraMonitoradoRepository{
	List<AreaSeguraMonitorado> findByIdAndCelular(long id, long celular);
	List<AreaSeguraMonitorado> findByIdMonitorado(long idMonitorado);
}
