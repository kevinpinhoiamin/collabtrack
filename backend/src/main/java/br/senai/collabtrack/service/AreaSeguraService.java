package br.senai.collabtrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.senai.collabtrack.entity.AreaSegura;
import br.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.senai.collabtrack.jpa.repository.AreaSeguraJpaRepository;
import br.senai.collabtrack.to.AreaSeguraTO;

@Service
public class AreaSeguraService {

	@Autowired
	private AreaSeguraJpaRepository areaSeguraJpaRepository;
	
	@Autowired
	private AreaSeguraMonitoradoService areaSeguraMonitoradoService;
	
	/**
	 * Método responsável por fazer o cadastro da área segura e de seus monitorados associados
	 * @param areaSeguraTO Instância do objeto de transferência da área segura
	 * @return Retorna o objeto de transferência com os IDs gerados
	 */
	@Transactional
	public AreaSeguraTO save(AreaSeguraTO areaSeguraTO) {
		
		// Cadastra a área segura
		AreaSegura areaSeguraDB = areaSeguraJpaRepository.save(areaSeguraTO.getAreaSegura());
		
		// Associa a área segura com o ID gerado e relaciona com o monitorado
		for(AreaSeguraMonitorado areaSeguraMonitorado : areaSeguraTO.getAreaSeguraMonitorados()) {
			areaSeguraMonitorado.setAreaSegura(areaSeguraDB);
		}
		
		// Cadastra a relação entre a área segura e os monitorados
		areaSeguraMonitoradoService.save(areaSeguraTO.getAreaSeguraMonitorados());
		
		areaSeguraTO.setAreaSegura(areaSeguraDB);
		return areaSeguraTO;
	}
	
	/**
	 * Faz a edição dos dados da área segura e a alteração das associações com os monitorados
	 * @param areaSeguraTO Instância do objeto de transferência da área segura
	 * @return Instância do objeto de transferência da área segura
	 */
	@Transactional
	public AreaSeguraTO update(AreaSeguraTO areaSeguraTO) {
		
		// Altera os dados da área segura
		AreaSegura areaSegura = areaSeguraJpaRepository.findOne(areaSeguraTO.getAreaSegura().getId());
		areaSegura.setCor(areaSeguraTO.getAreaSegura().getCor());
		areaSegura.setCorBorda(areaSeguraTO.getAreaSegura().getCorBorda());
		areaSegura.setNome(areaSeguraTO.getAreaSegura().getNome());
		areaSegura.setRaio(areaSeguraTO.getAreaSegura().getRaio());
		areaSeguraJpaRepository.save(areaSegura);
		
		// Exclui a associação da área segura com os monitorados
		areaSeguraMonitoradoService.delete(areaSeguraMonitoradoService.find(areaSeguraTO.getAreaSegura().getId(), areaSeguraTO.getMonitor().getId()));
		
		// Cadastra novamente as associações
		areaSeguraMonitoradoService.save(areaSeguraTO.getAreaSeguraMonitorados());
		
		return areaSeguraTO;
		
	}
	
	/**
	 * Método que exclui uma área segura e a associação com os monitorados
	 * @param id ID da área segura
	 * @param celular Número de celular do monitor
	 */
	@Transactional
	public void delete(long id, long celular) {
		areaSeguraMonitoradoService.delete(areaSeguraMonitoradoService.find(id, celular));
		areaSeguraJpaRepository.delete(id);
	}
	
}
