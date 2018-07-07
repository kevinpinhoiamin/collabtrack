package br.com.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.senai.collabtrack.entity.Status;
import br.com.senai.collabtrack.jpa.StatusRepository;

@Repository
@Transactional
public class StatusImplementation implements StatusRepository {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Status> findByMonitorCelular(long celular) throws NoResultException {
		String jpql = "SELECT s FROM status s, monitorado m, monitor_has_monitorado mm"
				+ " WHERE mm.primaryKey.monitorado = m"
				+ " AND mm.primaryKey.monitor.celular = :celular";
		
		TypedQuery<Status> query = entityManager.createQuery(jpql, Status.class);
		query.setParameter("celular", celular);
		
		return query.getResultList();
	}

}
