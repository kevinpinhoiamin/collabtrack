package br.senai.collabtrack.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.senai.collabtrack.entity.Monitor;
import br.senai.collabtrack.entity.MonitorMonitorado;
import br.senai.collabtrack.entity.MonitorMonitoradoPK;
import br.senai.collabtrack.entity.Monitorado;
import br.senai.collabtrack.entity.Status;
import br.senai.collabtrack.exception.CustomException;
import br.senai.collabtrack.firebase.FirebaseService;
import br.senai.collabtrack.jpa.MonitoradoRepository;
import br.senai.collabtrack.jpa.repository.MonitoradoJpaRepository;
import br.senai.collabtrack.to.FirebaseTO;
import br.senai.collabtrack.to.MonitoradoTO;
import br.senai.collabtrack.util.JsonUtil;

@Service
public class MonitoradoService {

	@Autowired
	private MonitoradoJpaRepository monitoradoJpaRepository;

	@Autowired
	private MonitoradoRepository monitoradoRepository;

	@Autowired
	private MonitorMonitoradoService monitorMonitoradoService;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private StatusService statusService;
	
	@Autowired
	private AreaSeguraMonitoradoService areaSeguraMonitoradoService;
	
	@Autowired
	private FirebaseService firebaseService;

	@Value("${firebase.action.cadastro_monitorado}")
	private int acaoCadastroMonitorado;
	
	@Value("${firebase.action.edicao_monitorado}")
	private int acaoEdicaoMonitorado;
	
	@Value("${firebase.action.remocao_monitorado}")
	private int acaoRemocaoMonitorado;
	
	public MonitoradoTO save(MonitoradoTO monitoradoTO) throws CustomException {

		// Variáveis
		Monitorado monitoradoDB = null;

		// Validação
		if (monitoradoTO == null) {
			throw new CustomException("Objeto não definido");
		} else if (monitoradoTO.getMonitorado() == null) {
			throw new CustomException("Monitorado não definido");
		} else if (monitoradoTO.getMonitorado().getId() == 0
				&& (monitoradoTO.getMonitorPrincipal() == null || monitoradoTO.getMonitorPrincipal().getId() == null
						|| monitoradoTO.getMonitorPrincipal().getId() <= 0)) {
			throw new CustomException("Nenhum monitor principal foi definido");
		} else if ((monitoradoTO.getMonitorado().getId() == 0
				|| (monitoradoTO.getMonitorado().getId() > 0 && monitoradoTO.getMonitorPrincipal() != null))
				&& monitorService.find(monitoradoTO.getMonitorPrincipal().getCelular()) == null) {
			throw new CustomException("Monitor principal não encontrado");
		} else {

			if (monitoradoTO.getMonitores() != null && monitoradoTO.getMonitores().size() > 0) {
				for (Monitor monitor : monitoradoTO.getMonitores()) {
					if (monitorService.find(monitor.getCelular()) == null) {
						throw new CustomException("Monitor secundário não encontrado");
					}
				}
			}

			try {

				Monitorado monitoradoValidacao = monitoradoJpaRepository
						.findFirstByCelular(monitoradoTO.getMonitorado().getCelular());
				if (monitoradoValidacao != null
						&& monitoradoValidacao.getId() != monitoradoTO.getMonitorado().getId()) {
					throw new CustomException("Número de celular já utilizado por outro monitorado");
				}

			} catch (NoResultException e) {
			}

			if (monitoradoTO.getMonitorado().getId() > 0) {
				try {
					monitoradoDB = monitoradoJpaRepository.findOne(monitoradoTO.getMonitorado().getId());
					if (monitoradoDB == null) {
						throw new NoResultException();
					}
				} catch (NoResultException e) {
					throw new CustomException("Monitorado não encontrado");
				}
			}

		}

		// Cadastro
		if (monitoradoTO.getMonitorado().getId() == 0) {

			// Cadastra o monitorado
			monitoradoDB = monitoradoJpaRepository.save(monitoradoTO.getMonitorado());
			// Cadastra a associação com o monitor principal
			MonitorMonitorado monitorMonitorado = new MonitorMonitorado();
			monitorMonitorado.setMonitor(monitoradoTO.getMonitorPrincipal());
			monitorMonitorado.setMonitorado(monitoradoDB);
			monitorMonitorado.setAtivo(true);
			monitorMonitorado.setPrincipal(true);
			monitorMonitorado.setCor(monitoradoTO.getCor());
			monitorMonitoradoService.save(monitorMonitorado);
			// Cadastra os monitores secundários
			if (monitoradoTO.getMonitores() != null && monitoradoTO.getMonitores().size() > 0) {
				for (Monitor monitor : monitoradoTO.getMonitores()) {
					if (monitor.isMarcado()) {
						monitorMonitorado = new MonitorMonitorado();
						monitorMonitorado.setMonitor(monitor);
						monitorMonitorado.setMonitorado(monitoradoDB);
						monitorMonitorado.setAtivo(true);
						monitorMonitorado.setCor(monitoradoTO.getCor());
						monitorMonitoradoService.save(monitorMonitorado);
					}
				}
			}
			// Cadastra o status do monitorado
			Status statusDB = statusService.save(new Status(monitoradoDB), false);

			// Associa o itens cadastrados ao TO
			monitoradoTO.setMonitorado(monitoradoDB);
			monitoradoTO.setStatus(statusDB);

			// Envia os dados do monitorado cadastrado para os outros monitores (com exceção
			// do autenticado)
			FirebaseTO firebaseTO = new FirebaseTO();
			firebaseTO.setAcao(acaoCadastroMonitorado);
			firebaseTO.setJson(new JsonUtil().toJson(monitoradoTO));
			if (monitoradoTO.getMonitores() != null && monitoradoTO.getMonitores().size() > 0) {

				for (Monitor monitor : monitoradoTO.getMonitores()) {
					if (monitor.getId() != monitoradoTO.getMonitorAutenticado().getId() && monitor.isMarcado()) {
						Monitor monitorDB = monitorService.find(monitor.getCelular());
						firebaseService.sendToMonitor(firebaseTO, monitorDB.getToken());
					}
				}

			}

			if (monitoradoTO.getMonitorPrincipal().getId() != monitoradoTO.getMonitorAutenticado().getId()) {
				Monitor monitorDB = monitorService.find(monitoradoTO.getMonitorPrincipal().getCelular());
				firebaseService.sendToMonitor(firebaseTO, monitorDB.getToken());
			}

			// Edição do monitor secundário
		} else if (monitoradoTO.getMonitorPrincipal() == null) {

			// Busca a relação entre o monitor secundário e o monitorado
			MonitorMonitorado monitorMonitoradoDB;
			try {
				monitorMonitoradoDB = monitorMonitoradoService.findFirstByPrimaryKey(
						new MonitorMonitoradoPK(monitoradoTO.getMonitores().get(0), monitoradoTO.getMonitorado()));
			} catch (NoResultException e) {
				throw new CustomException("Você não possui associação com o monitorado");
			}

			// Edita a cor
			monitorMonitoradoDB.setCor(monitoradoTO.getCor());
			monitorMonitoradoService.save(monitorMonitoradoDB);

			// Edição do monitor principal
		} else {

			// Altera o nome e o celular do monitorado
			monitoradoDB.setNome(monitoradoTO.getMonitorado().getNome());
			monitoradoDB.setCelular(monitoradoTO.getMonitorado().getCelular());

			// Monitor principal
			MonitorMonitorado monitorMonitoradoPrincipalDB = monitorMonitoradoService
					.findPrincipalByMonitoradoId(monitoradoTO.getMonitorado().getId());
			if (monitorMonitoradoPrincipalDB == null) {
				monitorMonitoradoPrincipalDB = new MonitorMonitorado();
				monitoradoTO.setMonitorPrincipal(monitoradoTO.getMonitorAutenticado());
				monitorMonitoradoPrincipalDB.setMonitor(monitoradoTO.getMonitorAutenticado());
			}

			if (monitoradoTO.getMonitorPrincipal().getId() == monitorMonitoradoPrincipalDB.getMonitor().getId()) {
				monitorMonitoradoPrincipalDB.setCor(monitoradoTO.getCor());
				monitorMonitoradoPrincipalDB.setPrincipal(true);
				monitorMonitoradoService.save(monitorMonitoradoPrincipalDB);
			} else {
				
				// Se foi alterado o monitor principal, remove a associação do monitorado com as áreas seguras
				if (monitoradoTO.getMonitorPrincipal().getId() != monitoradoTO.getMonitorAutenticado().getId()) {
					areaSeguraMonitoradoService.delete(monitoradoTO.getMonitorado().getId());
				}
				
				// Exclui a relação com antigo monitor principal
				monitorMonitoradoService.delete(monitorMonitoradoPrincipalDB);

				MonitorMonitorado monitorMonitorado = monitorMonitoradoService.findFirstByPrimaryKey(
						new MonitorMonitoradoPK(monitoradoTO.getMonitorPrincipal(), monitoradoDB));
				// Se já houver um associação entre o monitor que será o principal e o
				// monitorado
				if (monitorMonitorado != null) {
					monitorMonitorado.setPrincipal(true);
					monitorMonitoradoService.save(monitorMonitorado);

					// Coloca o novo monitor principal no objeto de transferência
					monitoradoTO.setMonitorPrincipal(monitorMonitorado.getMonitor());
				} else {

					// Cadastra o novo monitor principal
					MonitorMonitorado monitorMonitoradoPrincipal = new MonitorMonitorado();
					monitorMonitoradoPrincipal.setAtivo(true);
					monitorMonitoradoPrincipal.setCor(monitoradoTO.getCor());
					monitorMonitoradoPrincipal.setEmMonitoramento(false);
					monitorMonitoradoPrincipal.setMonitor(monitoradoTO.getMonitorPrincipal());
					monitorMonitoradoPrincipal.setMonitorado(monitoradoTO.getMonitorado());
					monitorMonitoradoPrincipal.setPrincipal(true);
					monitorMonitoradoService.save(monitorMonitoradoPrincipal);

					// Coloca o novo monitor principal no objeto de transferência
					monitoradoTO.setMonitorPrincipal(monitorMonitoradoPrincipal.getMonitor());

				}

			}

			// Busca o status, porém envia só o código
			Status status = statusService.findFirstByMonitorado(monitoradoTO.getMonitorado());

			// Cadastra os monitores secundários
			if (monitoradoTO.getMonitores() != null && monitoradoTO.getMonitores().size() > 0) {
				for (int i = 0; i < monitoradoTO.getMonitores().size(); i++) {

					Monitor monitor = monitoradoTO.getMonitores().get(i);
					MonitorMonitorado monitorMonitoradoDB = monitorMonitoradoService
							.findFirstByPrimaryKey(new MonitorMonitoradoPK(monitor, monitoradoTO.getMonitorado()));
					if (monitorMonitoradoDB != null) {

						// Se não for o montor principal e se o monitor não está marcado, faz a exclusão
						if (monitor.getId() != monitoradoTO.getMonitorPrincipal().getId() && !monitor.isMarcado()) {
							monitorMonitoradoService.delete(monitorMonitoradoDB);
						}

					} else {

						// Cadastra o monitor secundário se está maracado
						if (monitor.isMarcado()) {
							MonitorMonitorado monitorMonitoradoSecundario = new MonitorMonitorado();
							monitorMonitoradoSecundario.setAtivo(true);
							monitorMonitoradoSecundario.setCor(monitoradoTO.getCor());
							monitorMonitoradoSecundario.setEmMonitoramento(false);
							monitorMonitoradoSecundario.setMonitor(monitor);
							monitorMonitoradoSecundario.setMonitorado(monitoradoTO.getMonitorado());
							monitorMonitoradoSecundario.setPrincipal(false);
							monitorMonitoradoService.save(monitorMonitoradoSecundario);
						}

					}

					// Envia os dados do monitorado editado para os outros monitores (com exceção do
					// autenticado)
					if (monitor.getId() != monitorMonitoradoPrincipalDB.getMonitor().getId()) {

						Monitor monitorDB = monitorService.find(monitor.getCelular());

						FirebaseTO firebaseTO = new FirebaseTO();
						firebaseTO.setAcao(
								monitor.isMarcado() || monitoradoTO.getMonitorPrincipal().getId() == monitor.getId()
										? acaoEdicaoMonitorado
										: acaoRemocaoMonitorado);

						if (firebaseTO.getAcao() == acaoEdicaoMonitorado) {
							firebaseTO.setJson(new JsonUtil().toJson(monitoradoTO));
						} else if (firebaseTO.getAcao() == acaoRemocaoMonitorado) {
							firebaseTO.setId(monitoradoTO.getMonitorado().getId());
						} else if (status instanceof Status) {
							firebaseTO.setId(status.getId());
						}
						
						firebaseService.sendToMonitor(firebaseTO, monitorDB.getToken());

					}

				}

			}

		}

		return monitoradoTO;
	}

	public void delete(Long id) {
		monitoradoJpaRepository.delete(id);
	}

	public Monitorado findFirstByCelular(long celular) {
		return monitoradoJpaRepository.findFirstByCelular(celular);
	}

	public Monitorado findOne(Long id) {
		return monitoradoJpaRepository.findOne(id);
	}

	public List<Monitorado> findAll() {
		return monitoradoJpaRepository.findAll();
	}

	public MonitoradoTO findByMonitoradoId(long idMonitorado) {

		MonitoradoTO monitoradoTO = new MonitoradoTO();
		List<Monitor> monitorList = new ArrayList<>();
		List<MonitorMonitorado> monitorMonitoradoList = monitorMonitoradoService.findByMonitoradoId(idMonitorado);

		if (monitorMonitoradoList != null && monitorMonitoradoList.size() > 0) {

			for (MonitorMonitorado monitorMonitorado : monitorMonitoradoList) {

				if (monitorMonitorado.isPrincipal()) {
					monitoradoTO.setMonitorPrincipal(monitorMonitorado.getMonitor());
				} else {
					monitorList.add(monitorMonitorado.getMonitor());
				}

			}

			monitoradoTO.setMonitores(monitorList);

		}

		return monitoradoTO;

	}

	public Monitorado updateToken(long id, String token) throws CustomException {
		try {
			Monitorado monitorado = monitoradoJpaRepository.findOne(id);
			monitorado.setToken(token);
			return monitoradoJpaRepository.save(monitorado);
		} catch (NoResultException e) {
			throw new CustomException("Falha ao atualizar o token");
		}
	}

	public boolean isEmMonitoramento(long idMonitorado) {
		return monitoradoRepository.isEmMonitoramento(idMonitorado);
	}

	public List<Monitorado> findByMonitorIdPrincipal(long idMonitor) {
		return monitoradoRepository.findByMonitorIdPrincipal(idMonitor);
	}

}
