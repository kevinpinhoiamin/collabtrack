package br.com.senai.collabtrack.jpa.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.senai.collabtrack.entity.Mensagem;

public interface MensagemJpaRepository extends CrudRepository<Mensagem, Long> {

}
