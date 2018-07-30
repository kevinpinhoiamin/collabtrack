package br.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.senai.collabtrack.entity.Localizacao;

public interface LocalizacaoJpaRepository extends JpaRepository<Localizacao, Long> {

}
