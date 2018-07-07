package br.com.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.senai.collabtrack.entity.Monitor;
import br.com.senai.collabtrack.jpa.MonitorRepository;

@Repository
@Transactional
public class MonitorImplementation implements MonitorRepository{

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Monitor> findAtivoAndEmMonitoramentoByMonitoradoId(long idMonitorado) {
		String jpql = "SELECT m FROM monitor m, monitor_has_monitorado mm"
				+ " WHERE m = mm.primaryKey.monitor"
				+ " AND mm.ativo = true"
				+ " AND mm.emMonitoramento = true"
				+ " AND mm.primaryKey.monitorado.id = :idMonitorado";
		
		TypedQuery<Monitor> query = entityManager.createQuery(jpql, Monitor.class);
		query.setParameter("idMonitorado", idMonitorado);
		return query.getResultList();
	}

	@Override
	public List<Monitor> findOthersRelated(long idMonitor, long idMonitorado) {
		String jpql = "SELECT m FROM monitor m, monitor_has_monitorado mm"
				+ " WHERE m = mm.primaryKey.monitor"
				+ " AND mm.primaryKey.monitor.id != :idMonitor"
				+ " AND mm.primaryKey.monitorado.id = :idMonitorado"
				+ " AND mm.ativo = true";
		
		TypedQuery<Monitor> query = entityManager.createQuery(jpql, Monitor.class);
		query.setParameter("idMonitor", idMonitor);
		query.setParameter("idMonitorado", idMonitorado);
		return query.getResultList();
	}

	@Override
	public Monitor findPrincipalByMonitoradoCelular(long celular) {
		String jpql = "SELECT m1 FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1"
				+ "   AND mm.primaryKey.monitorado = m2 "
				+ "   AND m2.celular = :celular"
				+ "   AND mm.principal = true";
		
		TypedQuery<Monitor> query = entityManager.createQuery(jpql, Monitor.class);
		query.setParameter("celular", celular);
		
		return query.getSingleResult();
	}

	@Override
	public Monitor buscaPrincipalByCelularMonitorado(long celular) {
		String jpql = "SELECT m1 FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1"
				+ " AND mm.primaryKey.monitorado = m2"
				+ " AND m2.celular = :celular"
				+ " AND mm.principal = true";
		
		TypedQuery<Monitor> query = entityManager.createQuery(jpql, Monitor.class);
		query.setParameter("celular", celular);
		
		return query.getResultList().size() > 0 ? query.getSingleResult() : null;
  }
  
	public Monitor findByMonitoradoCelularAndTokenAutenticacao(long celularMonitorado, String tokenAutenticacao) throws NoResultException {
		String jpql = "SELECT m1 FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1"
				+ "   AND mm.primaryKey.monitorado = m2 "
				+ "   AND m2.celular = :celularMonitorado"
				+ "   AND mm.primaryKey.monitor.tokenAutenticacao = :tokenAutenticacao"
				+ "   AND mm.principal = true";
		
		TypedQuery<Monitor> query = entityManager.createQuery(jpql, Monitor.class);
		query.setParameter("celularMonitorado", celularMonitorado);
		query.setParameter("tokenAutenticacao", tokenAutenticacao);
		
		return query.getSingleResult();
	}
	
}
