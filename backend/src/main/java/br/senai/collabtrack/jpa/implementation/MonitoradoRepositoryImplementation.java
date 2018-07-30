package br.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.senai.collabtrack.entity.Monitorado;
import br.senai.collabtrack.jpa.MonitoradoRepository;

@Repository
@Transactional
public class MonitoradoRepositoryImplementation implements MonitoradoRepository {
	
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public boolean isEmMonitoramento(long idMonitorado) {
		String jpql = "SELECT m FROM monitorado m, monitor_has_monitorado mm "
				+ "WHERE mm.primaryKey.monitorado = m "
				+ "AND mm.emMonitoramento = true "
				+ "AND m.id = :idMonitorado";
	
		TypedQuery<Monitorado> query = entityManager.createQuery(jpql, Monitorado.class);
		query.setParameter("idMonitorado", idMonitorado);
		if(query.getResultList().size() > 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<Monitorado> findByMonitorIdPrincipal(long idMonitor) {
		String jpql = "SELECT m from monitorado m, monitor_has_monitorado mm "
				+ "WHERE mm.primaryKey.monitorado = m "
				+ "AND mm.principal = true "
				+ "AND mm.primaryKey.monitor.id = :idMonitor";
		TypedQuery<Monitorado> query = entityManager.createQuery(jpql, Monitorado.class);
		query.setParameter("idMonitor", idMonitor);
		return query.getResultList();
	}

}
