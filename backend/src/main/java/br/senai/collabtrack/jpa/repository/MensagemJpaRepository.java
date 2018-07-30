package br.senai.collabtrack.jpa.repository;

import org.springframework.data.repository.CrudRepository;

import br.senai.collabtrack.entity.Mensagem;

public interface MensagemJpaRepository extends CrudRepository<Mensagem, Long> {

}
