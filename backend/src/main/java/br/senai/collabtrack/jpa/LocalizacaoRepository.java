package br.senai.collabtrack.jpa;

import java.util.List;

import br.senai.collabtrack.entity.Localizacao;

public interface LocalizacaoRepository {
	List<Localizacao> search(long monitoradoId, int periodo, int pontos);
}
