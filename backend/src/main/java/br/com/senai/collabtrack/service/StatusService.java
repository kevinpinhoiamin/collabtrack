package br.com.senai.collabtrack.service;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.senai.collabtrack.entity.Monitor;
import br.com.senai.collabtrack.entity.Monitorado;
import br.com.senai.collabtrack.entity.Status;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.firebase.FirebaseService;
import br.com.senai.collabtrack.jpa.StatusRepository;
import br.com.senai.collabtrack.jpa.repository.StatusJpaRepository;
import br.com.senai.collabtrack.to.FirebaseTO;
import br.com.senai.collabtrack.util.DateUtil;
import br.com.senai.collabtrack.util.JsonUtil;

@Service
public class StatusService {

	@Autowired
	private StatusJpaRepository statusJpaRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private MonitoradoService monitoradoService;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MensagemService mensagemService;
	
	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private DateUtil dateUtil;
	
	@Value("${firebase.action.atualizar_status}")
	private int acaoAtualizarStatus;

	public List<Status> findByMonitorCelular(long celular) {
		try {
			return statusRepository.findByMonitorCelular(celular);
		} catch (NoResultException e) {
			return null;
		}
	}

	public Status save(Status status, boolean sendToMonitores) throws CustomException {

		// Verifica se o status é valido
		if (status == null || status.getMonitorado() == null || status.getMonitorado().getId() <= 0) {
			throw new CustomException("Status não definido ou monitorado ausente");
		}

		// Verificar se o monitorado indicado no status existe
		Monitorado monitorado = monitoradoService.findOne(status.getMonitorado().getId());
		if (monitorado == null) {
			throw new CustomException("Monitorado não encontrado");
		}

		// Salva ou altera o status
		Status statusDB = this.findFirstByMonitorado(monitorado);
		if (statusDB == null) {
			status.setData(null);
			statusDB = statusJpaRepository.save(status);
		} else {
			statusDB.setBateria(status.getBateria());
			statusDB.setData(new Date());
			statusDB.setInternetMovel(status.isInternetMovel());
			statusDB.setLocalizacao(status.isLocalizacao());
			statusDB.setWifi(status.isWifi());
			statusJpaRepository.save(statusDB);
		}

		if (sendToMonitores) {
			// Envia as mensagens para os monitores, indicando que a bateria está baixa ou
			// que a localização está desliagada
			mensagemService.saveStatus(statusDB, monitorado);

			// Manda o status atualizado a todos os monitores
			List<Monitor> monitores = monitorService
					.findAtivoAndEmMonitoramentoByMonitoradoId(statusDB.getMonitorado().getId());
			if (monitores != null && monitores.size() > 0) {

				FirebaseTO firebaseTO = new FirebaseTO();
				firebaseTO.setAcao(acaoAtualizarStatus);
				firebaseTO.setJson(new JsonUtil().toJson(statusDB));
				firebaseTO.setMensagem(dateUtil.format(statusDB.getData()));

				for (Monitor monitor : monitores) {
					firebaseService.sendToMonitor(firebaseTO, monitor.getToken());
				}
			}

		}

		return statusDB;

	}

	public Status findFirstByMonitorado(Monitorado monitorado) {
		try {
			return statusJpaRepository.findFirstByMonitorado(monitorado);
		} catch (NoResultException e) {
			return null;
		}
	}

}
