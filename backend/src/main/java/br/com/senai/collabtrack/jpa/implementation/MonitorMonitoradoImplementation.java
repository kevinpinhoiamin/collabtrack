package br.com.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.senai.collabtrack.entity.MonitorMonitorado;
import br.com.senai.collabtrack.jpa.MonitorMonitoradoRepository;

@Repository
@Transactional
public class MonitorMonitoradoImplementation implements MonitorMonitoradoRepository {

	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public List<MonitorMonitorado> findByMonitorId(long idMonitor) {
		String jpql = "SELECT mm FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1 AND"
				+ " mm.primaryKey.monitorado = m2 AND"
				+ " mm.primaryKey.monitor.id = :idMonitor";
		
		TypedQuery<MonitorMonitorado> query = entityManager.createQuery(jpql, MonitorMonitorado.class);
		query.setParameter("idMonitor", idMonitor);
		
		return query.getResultList();
	}

	@Override
	public List<MonitorMonitorado> findByMonitoradoId(long idMonitorado) {
		String jpql = "SELECT mm FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1 AND"
				+ " mm.primaryKey.monitorado = m2 AND"
				+ " mm.primaryKey.monitorado.id = :idMonitorado";
		
		TypedQuery<MonitorMonitorado> query = entityManager.createQuery(jpql, MonitorMonitorado.class);
		query.setParameter("idMonitorado", idMonitorado);
		
		return query.getResultList();
	}

	@Override
	public MonitorMonitorado findPrincipalByMonitoradoId(long idMonitorado) throws NoResultException {
		String jpql = "SELECT mm FROM monitor_has_monitorado mm, monitor m1, monitorado m2"
				+ " WHERE mm.primaryKey.monitor = m1 AND"
				+ " mm.primaryKey.monitorado = m2 AND"
				+ " mm.principal = true AND"
				+ " mm.primaryKey.monitorado.id = :idMonitorado";
		
		TypedQuery<MonitorMonitorado> query = entityManager.createQuery(jpql, MonitorMonitorado.class);
		query.setParameter("idMonitorado", idMonitorado);
		
		return query.getSingleResult();
	}

	@Override
	public boolean isEmMonitoramento(long idMonitorado) {
		String jpql = "SELECT mm FROM monitor_has_monitorado mm, monitorado m "
				+ "WHERE mm.primaryKey.monitorado = m "
				+ "AND mm.emMonitoramento = true "
				+ "AND m.id = :id_monitorado";
	
		TypedQuery<MonitorMonitorado> query = entityManager.createQuery(jpql, MonitorMonitorado.class);
		query.setParameter("id_monitorado", idMonitorado);
		if(query.getResultList().size() > 0) {
			return true;
		}
		
		return false;
	}

}
