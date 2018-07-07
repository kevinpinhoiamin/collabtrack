package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.Localizacao;

public interface LocalizacaoRepository {
	List<Localizacao> search(long monitoradoId, int periodo, int pontos);
}
