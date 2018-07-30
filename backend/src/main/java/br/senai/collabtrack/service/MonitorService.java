package br.senai.collabtrack.service;

import java.io.File;
import java.util.List;

import javax.persistence.NoResultException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.senai.collabtrack.entity.Monitor;
import br.senai.collabtrack.entity.Monitorado;
import br.senai.collabtrack.exception.CustomException;
import br.senai.collabtrack.firebase.FirebaseService;
import br.senai.collabtrack.jpa.MonitorRepository;
import br.senai.collabtrack.jpa.repository.MonitorJpaRepository;
import br.senai.collabtrack.to.FirebaseTO;
import br.senai.collabtrack.util.FileUtil;

@Service
public class MonitorService {

	@Autowired
	private MonitorJpaRepository monitorJpaRepository;

	@Autowired
	private MonitorRepository monitorRepository;
	
	@Autowired
	private MonitoradoService monitoradoService;
	
	@Autowired
	private FirebaseService firebaseService;
	
	@Value("${application.upload.minitor.default.image.extension}")
	private String monitorFileExtension;
	
	@Value("${application.upload.monitor}")
	private String uploadPathMonitor;
	
	@Value("${application.upload.monitor.default.image}")
	private String monitorDefaultPicture;
	
	@Value("${firebase.action.alterar_foto_perfil}")
	private int acaoAlterarFotoPerfil;
	
	@Value("${firebase.action.alterar_dados_monitor}")
	private int acaoAlterarDadosPerfil;

	public Monitor save(@Valid Monitor monitor) throws CustomException {
		if (monitorJpaRepository.findFirstByCelular(monitor.getCelular()) != null) {
			throw new CustomException("Número de celular já utilizado por outro monitor");
		}

		return monitorJpaRepository.save(monitor);
	}

	public Monitor update(Monitor monitor) throws CustomException {

		Monitor monitorDB = monitorJpaRepository.getOne(monitor.getId());
		if (monitorDB == null) {
			throw new CustomException("Monitor não encontrado");
		}

		monitorDB.setNome(monitor.getNome());
		monitorDB.setCelular(monitor.getCelular());

		if (monitor.getToken() != null && !monitor.getToken().equals("")) {
			monitorDB.setToken(monitor.getToken());
		}

		monitorDB = monitorJpaRepository.save(monitorDB);
		
		// Envia um ping para o monitorado avisando que os dados do monitor foram alterados
		List<Monitorado> monitorados = monitoradoService.findByMonitorIdPrincipal(monitorDB.getId());
		if(monitorados != null && monitorados.size() > 0) {
			
			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setAcao(acaoAlterarDadosPerfil);
			for(Monitorado monitorado : monitorados) {
				firebaseService.sendToMonitor(firebaseTO, monitorado.getToken());
			}
			
		}
		
		return monitorDB;

	}

	public List<Monitor> findAll() {
		return monitorJpaRepository.findAll();
	}

	public void updatePicture(long idMonitor, String fileName, String base64) throws CustomException {

		Monitor monitor = monitorJpaRepository.findOne(idMonitor);
		if (monitor == null) {
			throw new CustomException("Monitor não encontrado");
		} else if (fileName == null) {
			throw new CustomException("Informe o nome do arquivo");
		} else if (base64 == null) {
			throw new CustomException("Informe o arquivo como Base64");
		}

		FileUtil fileUtil = new FileUtil();

		// Deleta a foto antiga, caso tenha
		fileUtil.deleteFile(monitor.getId() + monitorFileExtension,
				uploadPathMonitor);

		// Faz o upload (salva o arquivo em uma pasta)
		fileUtil.upload(fileName, String.valueOf(monitor.getId()), base64, uploadPathMonitor);
		
		// Envia um ping para o monitorado avisando que a foto foi alterada
		List<Monitorado> monitorados = monitoradoService.findByMonitorIdPrincipal(idMonitor);
		if(monitorados != null && monitorados.size() > 0) {
			
			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setAcao(acaoAlterarFotoPerfil);
			for(Monitorado monitorado : monitorados) {
				firebaseService.sendToMonitor(firebaseTO, monitorado.getToken());
			}
			
		}
		
	}

	public File findPicture(long celular) throws CustomException {
		Monitor monitor = this.find(celular);

		FileUtil fileUtil = new FileUtil();
		File file = fileUtil.getFile(monitor.getId() + monitorFileExtension,
				uploadPathMonitor);
		if (file.exists() && !file.isDirectory()) {
			return file;
		} else {
			return fileUtil.getFile(monitorDefaultPicture,
					uploadPathMonitor);
		}
	}

	public Monitor find(long celular) throws CustomException {
		Monitor monitor = monitorJpaRepository.findFirstByCelular(celular);
		if (monitor == null) {
			throw new CustomException("Monitor não encontrado");
		}
		return monitor;
	}

	public Monitor find(long celular, String tokenAutenticacao) throws CustomException {
		Monitor monitor = monitorJpaRepository.findFirstByCelularAndTokenAutenticacao(celular, tokenAutenticacao);
		if (monitor == null) {
			throw new CustomException("Monitor não encontrado");
		}
		return monitor;
	}

	public Monitor logout(Monitor monitor) throws CustomException {

		Monitor monitorDB = monitorJpaRepository.findOne(monitor.getId());
		if (monitorDB == null) {
			throw new CustomException("Monitor não encontrado");
		}

		monitorDB.setToken("");
		return monitorJpaRepository.save(monitorDB);

	}

	public List<Monitor> findAtivoAndEmMonitoramentoByMonitoradoId(long idMonitorado) {
		return monitorRepository.findAtivoAndEmMonitoramentoByMonitoradoId(idMonitorado);
	}

	public List<Monitor> findOthersRelated(long idMonitor, long idMonitorado) {
		return monitorRepository.findOthersRelated(idMonitor, idMonitorado);
	}

	public Monitor findPrincipalByMonitoradoCelular(long celular) {
		try {
			return monitorRepository.findPrincipalByMonitoradoCelular(celular);
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Monitor findByMonitoradoCelularAndTokenAutenticacao(long celularMonitorado, String tokenAutenticacao) {
		try {
			return monitorRepository.findByMonitoradoCelularAndTokenAutenticacao(celularMonitorado, tokenAutenticacao);
		} catch (NoResultException e) {
			return null;
		}
	}

	public Monitor findOne(long idMonitor) {
		return monitorJpaRepository.findOne(idMonitor);
	}

	public Monitor buscaMonitorPrincipalByCelularMonitorado(long celular) {
		return monitorRepository.buscaPrincipalByCelularMonitorado(celular);
	}

}
