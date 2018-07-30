package br.senai.collabtrack.service;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.senai.collabtrack.entity.MonitorMonitorado;
import br.senai.collabtrack.entity.MonitorMonitoradoPK;
import br.senai.collabtrack.exception.CustomException;
import br.senai.collabtrack.firebase.FirebaseService;
import br.senai.collabtrack.jpa.MonitorMonitoradoRepository;
import br.senai.collabtrack.jpa.repository.MonitorMonitoradoJpaRepository;
import br.senai.collabtrack.to.FirebaseTO;

@Service
public class MonitorMonitoradoService {

	@Autowired
	private MonitorMonitoradoJpaRepository monitorMonitoradoJpaRepository;

	@Autowired
	private MonitorMonitoradoRepository monitorMonitoradoRepository;

	@Autowired
	private FirebaseService firebaseService;
	
	@Value("${firebase.action.iniciar_monitoramento}")
	private int acaoIniciarMonitoramento;
	
	@Value("${firebase.action.monitoramento.start}")
	private String iniciarMonitoramentoStart;
	
	@Value("${firebase.action.monitoramento.stop}")
	private String iniciarMonitoramentoStop;
	
	public List<MonitorMonitorado> findByMonitorId(long idMonitor) {
		return monitorMonitoradoRepository.findByMonitorId(idMonitor);
	}

	public MonitorMonitorado findFirstByPrimaryKey(MonitorMonitoradoPK primaryKey) {
		try {
			return monitorMonitoradoJpaRepository.findFirstByPrimaryKey(primaryKey);
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<MonitorMonitorado> findByMonitoradoId(long idMonitorado) {
		return monitorMonitoradoRepository.findByMonitoradoId(idMonitorado);
	}

	public MonitorMonitorado findPrincipalByMonitoradoId(long idMonitorado) {
		try {
			return monitorMonitoradoRepository.findPrincipalByMonitoradoId(idMonitorado);
		} catch (NoResultException e) {
			return null;
		}
	}

	public MonitorMonitorado save(MonitorMonitorado monitorMonitorado) {
		return monitorMonitoradoJpaRepository.save(monitorMonitorado);
	}

	public void delete(MonitorMonitorado monitorMonitorado) {
		monitorMonitoradoJpaRepository.delete(monitorMonitorado);
	}

	public void monitorar(MonitorMonitorado monitorMonitorado) throws CustomException {

		MonitorMonitorado monitorMonitoradoDB = null;
		if (monitorMonitorado == null) {
			throw new CustomException("Objeto não definido");
		} else if (monitorMonitorado.getMonitor() == null || monitorMonitorado.getMonitor().getId() <= 0) {
			throw new CustomException("Monitor não deifnido");
		} else {
			try {
				monitorMonitoradoDB = monitorMonitoradoJpaRepository
						.findFirstByPrimaryKey(monitorMonitorado.getPrimaryKey());
			} catch (NoResultException e) {
				throw new CustomException("Monitor ou monitorado inválido");
			}
		}

		if (monitorMonitorado.getMonitorado() != null && monitorMonitorado.getMonitorado().getId() > 0) {
			monitorar(monitorMonitoradoDB, monitorMonitorado.isEmMonitoramento());
		} else {
			List<MonitorMonitorado> monitorMonitoradoList = monitorMonitoradoRepository
					.findByMonitorId(monitorMonitorado.getMonitor().getId());
			if (monitorMonitoradoList != null && monitorMonitoradoList.size() > 0) {
				for (MonitorMonitorado monitorMonitoradoIteracao : monitorMonitoradoList) {
					monitorar(monitorMonitoradoIteracao, monitorMonitorado.isEmMonitoramento());
				}
			}
		}

	}

	private void monitorar(MonitorMonitorado monitorMonitorado, boolean monitorar) {

		if (monitorMonitorado == null || !monitorMonitorado.isAtivo()) {
			return;
		}

		monitorMonitorado.setEmMonitoramento(monitorar);
		monitorMonitoradoJpaRepository.save(monitorMonitorado);

		FirebaseTO firebaseTO = new FirebaseTO();
		firebaseTO.setAcao(acaoIniciarMonitoramento);

		// Só manda parar o monitoramento se mais ninguém estiver monitorando o
		// monitorado
		if (!monitorar && !monitorMonitoradoRepository.isEmMonitoramento(monitorMonitorado.getMonitorado().getId())) {
			firebaseTO.setMensagem(iniciarMonitoramentoStop);
		} else {
			firebaseTO.setMensagem(iniciarMonitoramentoStart);
		}

		firebaseTO.setId(monitorMonitorado.getMonitor().getId());
		firebaseService.sendToMonitorado(firebaseTO, monitorMonitorado.getMonitorado().getToken());

	}
	
	public MonitorMonitorado update(MonitorMonitorado monitorMonitorado) throws CustomException {
		
		if(monitorMonitorado == null || 
				monitorMonitorado.getMonitor() == null || 
				monitorMonitorado.getMonitor().getId() == null || 
				monitorMonitorado.getMonitorado() == null || 
				monitorMonitorado.getMonitorado().getId() == null) {
			throw new CustomException("Objeto de requisição inválido");
		}
		
		try {
			MonitorMonitorado monitorMonitoradoDB = monitorMonitoradoJpaRepository.findFirstByPrimaryKey(monitorMonitorado.getPrimaryKey());
			monitorMonitoradoDB.setEmMonitoramento(monitorMonitorado.isEmMonitoramento());
			
			monitorar(monitorMonitoradoDB, monitorMonitoradoDB.isEmMonitoramento());
			
			monitorMonitoradoDB.setAtivo(monitorMonitorado.isAtivo());
			
			return monitorMonitoradoJpaRepository.save(monitorMonitoradoDB);
		} catch (NoResultException e) {
			throw new CustomException("Monitorado não encontrado");
		}
		
	}

}
