package br.com.senai.collabtrack.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.senai.collabtrack.entity.Localizacao;

public interface LocalizacaoJpaRepository extends JpaRepository<Localizacao, Long> {

}
