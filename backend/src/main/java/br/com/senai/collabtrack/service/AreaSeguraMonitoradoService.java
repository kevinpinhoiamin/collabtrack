package br.com.senai.collabtrack.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.jpa.AreaSeguraMonitoradoRepository;
import br.com.senai.collabtrack.jpa.repository.AreaSeguraMonitoradoJpaRepository;

@Service
public class AreaSeguraMonitoradoService {

	@Autowired
	private AreaSeguraMonitoradoJpaRepository jpaRepository;
	
	@Autowired
	private AreaSeguraMonitoradoRepository repository;
	
	public void save(List<AreaSeguraMonitorado> areaSeguraMonitorados) {
		jpaRepository.save(areaSeguraMonitorados);
	}
	
	public List<AreaSeguraMonitorado> find(long id, long celular ){
		return repository.findByIdAndCelular(id, celular);
	}
	
	public List<AreaSeguraMonitorado> find(long celular) {
		return repository.findByIdAndCelular(0, celular);
	}
	
	public void delete(List<AreaSeguraMonitorado> areaSeguraMonitorados) {
		jpaRepository.delete(areaSeguraMonitorados);
	}
	
	public void delete(long idMonitorado) {
		List<AreaSeguraMonitorado> areaSeguraMonitorados = repository.findByIdMonitorado(idMonitorado);
		if (areaSeguraMonitorados != null && areaSeguraMonitorados.size() > 0) {
			jpaRepository.delete(areaSeguraMonitorados);
		}
	}
	
	public AreaSeguraMonitorado update(AreaSeguraMonitorado areaSeguraMonitorado) throws CustomException {
		AreaSeguraMonitorado areaSeguraMonitoradoDB = jpaRepository.findFistByPrimaryKey(areaSeguraMonitorado.getPrimaryKey());
	
		if(areaSeguraMonitoradoDB == null) {
			throw new CustomException("Área segura não está relacionada ao monitorado");
		}
		
		areaSeguraMonitoradoDB.setAtiva(areaSeguraMonitorado.isAtiva());

		return jpaRepository.save(areaSeguraMonitoradoDB);
	}
	
}
