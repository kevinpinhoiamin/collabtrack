package br.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.senai.collabtrack.jpa.AreaSeguraMonitoradoRepository;

@Repository
@Transactional
public class AreaSeguraMonitoradoImplementation implements AreaSeguraMonitoradoRepository{

	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public List<AreaSeguraMonitorado> findByIdAndCelular(long id, long celular) {
		String jpql = "SELECT am FROM area_segura_has_monitorado am, monitor_has_monitorado mm"
				+ " WHERE am.primaryKey.monitorado = mm.primaryKey.monitorado"
				+ " AND mm.primaryKey.monitor.celular = :celular"
				+ " AND mm.principal = true"
				+ (id > 0 ? " AND am.primaryKey.areaSegura.id = :id" : "");
	
		TypedQuery<AreaSeguraMonitorado> query = entityManager.createQuery(jpql, AreaSeguraMonitorado.class);
		query.setParameter("celular", celular);
		if(id > 0){
			query.setParameter("id", id);
		}
		
		return query.getResultList();
	}

	@Override
	public List<AreaSeguraMonitorado> findByIdMonitorado(long idMonitorado) {
		String jpql = "SELECT am FROM area_segura_has_monitorado am"
				+ " WHERE am.primaryKey.monitorado.id = :idMonitorado";
	
		TypedQuery<AreaSeguraMonitorado> query = entityManager.createQuery(jpql, AreaSeguraMonitorado.class);
		query.setParameter("idMonitorado", idMonitorado);
		
		return query.getResultList();
	}

}
