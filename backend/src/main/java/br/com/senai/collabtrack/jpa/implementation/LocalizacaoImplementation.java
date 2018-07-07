package br.com.senai.collabtrack.jpa.implementation;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.senai.collabtrack.entity.Localizacao;
import br.com.senai.collabtrack.jpa.LocalizacaoRepository;
import br.com.senai.collabtrack.util.DateUtil;

@Repository
@Transactional
public class LocalizacaoImplementation implements LocalizacaoRepository {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	DateUtil dateUtil;

	@Override
	public List<Localizacao> search(long monitoradoId, int periodo, int pontos) {
		
		Date data = dateUtil.getPeriodo(periodo);
		
		String jpql = "SELECT l FROM localizacao l, monitorado m "
				+ "WHERE l.monitorado = m "
				+ (data != null ? "AND l.data >= :data " : "")
				+ "AND m.id = :monitoradoId "
				+ "ORDER BY l.id DESC";
		
		TypedQuery<Localizacao> query = entityManager.createQuery(jpql, Localizacao.class);
		query.setParameter("monitoradoId", monitoradoId);
		if(data != null)
			query.setParameter("data", data, TemporalType.DATE);
		query.setMaxResults(pontos);
		
		return query.getResultList();
	}

}
