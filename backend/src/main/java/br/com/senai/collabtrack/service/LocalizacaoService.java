package br.com.senai.collabtrack.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.senai.collabtrack.entity.Localizacao;
import br.com.senai.collabtrack.entity.Monitor;
import br.com.senai.collabtrack.firebase.FirebaseService;
import br.com.senai.collabtrack.jpa.LocalizacaoRepository;
import br.com.senai.collabtrack.jpa.repository.LocalizacaoJpaRepository;
import br.com.senai.collabtrack.to.FirebaseTO;
import br.com.senai.collabtrack.util.DateUtil;
import br.com.senai.collabtrack.util.JsonUtil;

@Service
public class LocalizacaoService {

	@Autowired
	private LocalizacaoJpaRepository localizacaoJpaRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MonitoradoService monitoradoService;

	@Autowired
	private FirebaseService firebaseService;
	
	@Autowired
	private DateUtil dateUtil;
	
	@Value("${firebase.action.area_segura}")
	private int acaoAreaSegura;
	
	public Localizacao save(Localizacao localizacao) {

		localizacao.setData(new Date());
		localizacao.setMonitorado(monitoradoService.findOne(localizacao.getMonitorado().getId()));
		localizacao = localizacaoJpaRepository.save(localizacao);
		String tempo = dateUtil.getTime(localizacao.getData());

		List<Monitor> monitores = monitorService
				.findAtivoAndEmMonitoramentoByMonitoradoId(localizacao.getMonitorado().getId());

		if (monitores != null && monitores.size() > 0) {

			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setMensagem(tempo);
			firebaseTO.setAcao(acaoAreaSegura);
			firebaseTO.setJson(new JsonUtil().toJson(localizacao));

			for (Monitor monitor : monitores) {
				firebaseService.sendToMonitor(firebaseTO, monitor.getToken());
			}
		}

		return localizacao;

	}

	public List<Localizacao> search(long[] monitorados, int periodo, int pontos) {

		List<Localizacao> localizacoes = new ArrayList<>();

		if (monitorados != null && monitorados.length > 0) {

			for (long monitoradoId : monitorados) {
				if (monitoradoId > 0) {
					localizacoes.addAll(localizacaoRepository.search(monitoradoId, periodo, pontos));
				}
			}

		}

		return localizacoes;

	}

}
