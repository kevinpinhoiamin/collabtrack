package br.com.senai.collabtrack.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.senai.collabtrack.entity.Mensagem;
import br.com.senai.collabtrack.entity.Monitor;
import br.com.senai.collabtrack.entity.MonitorMonitorado;
import br.com.senai.collabtrack.entity.MonitorMonitoradoPK;
import br.com.senai.collabtrack.entity.Monitorado;
import br.com.senai.collabtrack.entity.Status;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.firebase.FirebaseService;
import br.com.senai.collabtrack.jpa.MensagemRepository;
import br.com.senai.collabtrack.jpa.repository.MensagemJpaRepository;
import br.com.senai.collabtrack.to.FirebaseTO;
import br.com.senai.collabtrack.to.RespostaTO;
import br.com.senai.collabtrack.util.DateUtil;
import br.com.senai.collabtrack.util.FileUtil;

@Service
public class MensagemService {

	@Autowired
	private MensagemJpaRepository mensagemJpaRepository;

	@Autowired
	private MensagemRepository mensagemRepository;

	@Autowired
	private MonitorMonitoradoService monitorMonitoradoService;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MonitoradoService monitoradoService;
	
	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private DateUtil dateUtil;
	
	@Value("${application.upload.audio}")
	private String uploadPathAudio;
	
	@Value("${application.time.type.minutes}")
	private int typeMinutes;
	
	@Value("${application.mensagem.type.audio}")
	private int mensagemTypeAudio;
	
	@Value("${application.mensagem.type.resposta}")
	private int mensagemTypeResposta;
	
	@Value("${application.mensagem.type.alerta}")
	private int mensagemTypeAlerta;
	
	@Value("${application.mensagem.type.diverso}")
	private int mensagemTypeDiverso;
	
	@Value("${application.mensagem.type.area_segura}")
	private int mensagemTypeAreaSegura;
	
	@Value("${application.mensagem.type.localizacao}")
	private int mensagemTypeLocalizacao;
	
	@Value("${firebase.action.audio}")
	private int acaoAudio;
	
	@Value("${firebase.action.resposta}")
	private int acaoResposta;
	
	@Value("${firebase.action.diversos}")
	private int acaoDiversos;
	
	@Value("${firebase.action.mensagem}")
	private int acaoMensagem;
	
	@Value("${application.upload.audio.default.file.extension}")
	private String audioFileExtension;
	
	public List<Mensagem> findByMonitorCelular(long celular) {
		return mensagemRepository.findByMonitorCelular(celular);
	}

	public Mensagem save(Mensagem mensagem) throws CustomException {

		MonitorMonitorado monitorMonitorado = monitorMonitoradoService
				.findFirstByPrimaryKey(new MonitorMonitoradoPK(	mensagem.getMonitor(), mensagem.getMonitorado()));
		if (monitorMonitorado == null) {
			throw new CustomException("Monitor ou monitorado não encontrado");
		}
		
		mensagem.setData(new Date());
		mensagem.setRespondido(false);
		Mensagem mensagemDB = mensagemJpaRepository.save(mensagem);

		List<Monitor> monitores;
		int tipos[] = {mensagemTypeResposta, mensagemTypeAlerta, mensagemTypeAreaSegura,
				mensagemTypeDiverso, mensagemTypeLocalizacao};
		if (IntStream.of(tipos).anyMatch(tipo -> tipo == mensagem.getTipo())) {
			monitores = monitorService.findOthersRelated(0, mensagem.getMonitorado().getId());
		} else {
			monitores = monitorService.findOthersRelated(mensagem.getMonitor().getId(),
					mensagem.getMonitorado().getId());
		}

		if(mensagem.getTipo() == mensagemTypeAreaSegura) {
			monitores = null;
		}else if (mensagem.getTipo() == mensagemTypeAudio) {
			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setAcao(acaoAudio);
			firebaseService.sendToMonitorado(firebaseTO, monitorMonitorado.getMonitorado().getToken());
		}
		
		if (monitores != null && monitores.size() > 0) {

			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setMensagem(mensagem.getMensagem());
			firebaseTO.setObjeto(mensagemDB);
			if (mensagem.getTipo() == mensagemTypeAudio) {
				firebaseTO.setAcao(acaoAudio);
			} else if (mensagem.getTipo() == mensagemTypeResposta) {
				firebaseTO.setAcao(acaoResposta);
			} else if (mensagem.getTipo() == mensagemTypeAlerta || mensagem.getTipo() == mensagemTypeDiverso
					|| mensagem.getTipo() == mensagemTypeAreaSegura || mensagem.getTipo() == mensagemTypeLocalizacao) {
				firebaseTO.setAcao(acaoDiversos);
			} else {
				firebaseTO.setAcao(acaoMensagem);
			}

			for (Monitor monitor : monitores) {
				firebaseService.sendToMonitor(firebaseTO, monitor.getToken());
			}

		}

		return mensagemDB;

	}

	public void saveStatus(Status status, Monitorado monitorado) throws CustomException {

		if (status == null) {
			throw new CustomException("Status inválido");
		} else if (monitorado == null) {
			throw new CustomException("Monitorado inválido");
		}

		Monitor monitor = monitorService.findPrincipalByMonitoradoCelular(monitorado.getCelular());
		if (monitor == null) {
			throw new CustomException("Monitor não encontrado");
		}

		// É necessário 2 instâncias de mensagem, caso contrário o Spring acha que está gerenciando a mesma mensagem, 
		// fazendo o update o invés de insert na segunda requisição		
		if (status.getBateria() <= 20) {
			Mensagem mensagemBaterria = new Mensagem();
			mensagemBaterria.setTipo(mensagemTypeDiverso);
			mensagemBaterria.setMonitor(monitor);
			mensagemBaterria.setMonitorado(monitorado);
			mensagemBaterria.setMensagem("Bateria em " + status.getBateria() + "%");
			this.save(mensagemBaterria);
		}
		
		long tempoUltimaMensagem = 0;
		Mensagem ultimaMensagemLocalizacao = null;
		Mensagem ultimaMensagem = null;
		try {
			ultimaMensagemLocalizacao = mensagemRepository.findFirstByMonitoradoIdAndTipo(monitorado.getId(), mensagemTypeLocalizacao);
			tempoUltimaMensagem = dateUtil.diferencaAteDataHoraAtual(ultimaMensagemLocalizacao.getData(), typeMinutes);
			ultimaMensagem = mensagemRepository.findFirstByMonitoradoId(monitorado.getId());
		} catch (NoResultException e) {
		}

		if ((ultimaMensagemLocalizacao == null || (tempoUltimaMensagem >= 15 && ultimaMensagem.getTipo() != mensagemTypeLocalizacao)) && !status.isLocalizacao()) {
			Mensagem mensagemLocalizacao = new Mensagem();
			mensagemLocalizacao.setTipo(mensagemTypeLocalizacao);
			mensagemLocalizacao.setMonitor(monitor);
			mensagemLocalizacao.setMonitorado(monitorado);
			mensagemLocalizacao.setMensagem("A localização está desativada!");
			this.save(mensagemLocalizacao);
		}

	}
	
	public Mensagem addAudio(MultipartFile file, long idMonitor, long idMonitorado) throws CustomException{
		if (file.isEmpty()) {
			throw new CustomException("Arquivo vazio");
		}

		Monitor monitor = monitorService.findOne(idMonitor);
		Monitorado monitorado = monitoradoService.findOne(idMonitorado);
		 
		Mensagem mensagem = new Mensagem();
		mensagem.setMensagem("Áudio");
		mensagem.setMonitor(monitor);
		mensagem.setMonitorado(monitorado);
		mensagem.setTipo(mensagemTypeAudio);
		Mensagem mensagemRetorno = save(mensagem);
		 
		String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."), file.getOriginalFilename().length());
		
		FileUtil fileUtil = new FileUtil();
		fileUtil.uploadFileSpring(file, uploadPathAudio + "/" + monitorado.getId(), mensagemRetorno.getId()+extensao);

		return mensagemRetorno;
	}
	
	public File buscarAudio(long audio, long monitorado) {
		return new FileUtil().getFile(audio + audioFileExtension, uploadPathAudio + "/" + monitorado);
	}

	public Long proximoDaFila(long monitorado) {
		try {
			return mensagemRepository.proximoAudioDaFila(monitorado).getId();			
		}catch(NoResultException e) {
			return (long) 0;
		}
	}

	public boolean salvarResposta(RespostaTO resposta) throws CustomException {
		Mensagem mensagemAudio = mensagemJpaRepository.findOne(resposta.getId());
		if(mensagemAudio != null) {
			mensagemAudio.setRespondido(true);
			mensagemJpaRepository.save(mensagemAudio);
			
			Mensagem mensagemResposta = new Mensagem();
			if(resposta.isResposta()) {
				mensagemResposta.setMensagem("Normalidade");
			}else {
				mensagemResposta.setMensagem("Perigo");
			}
			
			mensagemResposta.setMonitor(mensagemAudio.getMonitor());
			mensagemResposta.setMonitorado(mensagemAudio.getMonitorado());
			mensagemResposta.setResposta((int) resposta.getId());
			mensagemResposta.setTipo(mensagemTypeResposta);
			save(mensagemResposta);
		}else {
			return false;
		}
		return true;
	}

	public boolean salvarAviso(RespostaTO aviso) throws CustomException {
		Mensagem mensagemAviso = new Mensagem();
		if(aviso.isResposta()) {
			mensagemAviso.setMensagem("Normalidade");
		}else {
			mensagemAviso.setMensagem("Perigo");
		}
		
		Monitor monitor = new Monitor();
		monitor.setId((long) aviso.getIdMonitor());

		Monitorado monitorado = new Monitorado();
		monitorado.setId(aviso.getId());
		
		mensagemAviso.setMonitor(monitor);
		mensagemAviso.setMonitorado(monitorado);
		mensagemAviso.setTipo(mensagemTypeAlerta);
		save(mensagemAviso);

		return true;
	}

	public boolean salvarAvisoForaAreaSegura(RespostaTO aviso) throws CustomException {
		if(aviso.getId() > 0) {
			if(aviso.isResposta() == true) {
				boolean enviarAviso = false;
				try {
					Mensagem ultimaMensagem = mensagemRepository.ultimaMensagemAreaSegura(aviso.getId());
					long diferencaMin = dateUtil.diferencaAteDataHoraAtual(ultimaMensagem.getData(), typeMinutes);
					
					if(diferencaMin > 15) {
						enviarAviso = true;
					}
					
				}catch(NoResultException e) {
					enviarAviso = true;
				}
				
				if(enviarAviso) {
					Monitorado monitorado = monitoradoService.findOne(aviso.getId());
					Monitor monitor = monitorService.buscaMonitorPrincipalByCelularMonitorado(monitorado.getCelular());
					
					Mensagem mensagemAviso = new Mensagem();
					mensagemAviso.setMensagem("Fora de Área Segura");
					mensagemAviso.setTipo(mensagemTypeAreaSegura);
					mensagemAviso.setMonitor(monitor);
					mensagemAviso.setMonitorado(monitorado);
					
					save(mensagemAviso);
				}
			}else {
				return true;
			}
		}else {
			return false;
		}
		return true;
	}
	
}
