package br.senai.collabtrack.jpa.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.senai.collabtrack.entity.Mensagem;
import br.senai.collabtrack.jpa.MensagemRepository;

@Repository
@Transactional
public class MensagemImplementation implements MensagemRepository {

	@PersistenceContext
	EntityManager entityManager;
	
	@Value("${application.mensagem.type.area_segura}")
	private int mensagemTypeAreaSegura;

	@Override
	public List<Mensagem> findByMonitorCelular(long celular) {
		String jpql = "SELECT m FROM mensagem m, monitor_has_monitorado mm"
				+ " WHERE m.monitorado = mm.primaryKey.monitorado"
				+ " AND mm.primaryKey.monitor.celular = :celular"
				+ "	AND (m.tipo != "+mensagemTypeAreaSegura+" "
				+ "OR (m.tipo = "+mensagemTypeAreaSegura+" AND m.monitor.celular = :celular) )"
				+ " ORDER BY data DESC";

		TypedQuery<Mensagem> query = entityManager.createQuery(jpql, Mensagem.class);
		query.setParameter("celular", celular);
		query.setMaxResults(250);

		return query.getResultList();
	}

	@Override
	public Mensagem findFirstByMonitoradoIdAndTipo(long idMonitorado, int tipo) throws NoResultException {
		String jpql = "SELECT m FROM mensagem m "
				+ " WHERE m.monitorado.id = :idMonitorado"
				+ (tipo > 0 ? " AND m.tipo = :tipo" : "")
				+ "	ORDER BY id DESC";
		
		TypedQuery<Mensagem> query = entityManager.createQuery(jpql, Mensagem.class);
		query.setParameter("idMonitorado", idMonitorado);
		if (tipo > 0) {
			query.setParameter("tipo", tipo);
		}
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	@Override
	public Mensagem findFirstByMonitoradoId(long idMonitorado) {
		// TODO Auto-generated method stub
		return this.findFirstByMonitoradoIdAndTipo(idMonitorado, 0);
	}

	@Override
	public Mensagem proximoAudioDaFila(long monitorado) throws NoResultException{
		String jpql = "SELECT m FROM mensagem m"
				+ " WHERE m.monitorado.id = :monitorado"
				+ " AND m.respondido != true"
				+ " AND m.tipo = 2"
				+ " ORDER BY id ASC";
		
		TypedQuery<Mensagem> query = entityManager.createQuery(jpql, Mensagem.class);
		query.setParameter("monitorado", monitorado);
		query.setMaxResults(1);
		return query.getSingleResult();
		
	}

	@Override
	public Mensagem ultimaMensagemAreaSegura(long id) throws NoResultException{
		String jpql = "SELECT m FROM mensagem m"
				+ " WHERE m.monitorado.id = :id"
				+ " AND m.tipo = 6"
				+ " ORDER BY id DESC";
		
		TypedQuery<Mensagem> query = entityManager.createQuery(jpql, Mensagem.class);
		query.setParameter("id", id);
		query.setMaxResults(1);
		return query.getSingleResult();
	}

}
