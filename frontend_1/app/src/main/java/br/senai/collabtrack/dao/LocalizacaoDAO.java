package br.senai.collabtrack.dao;

import java.util.List;

import br.senai.collabtrack.domain.Localizacao;

/**
 * Created by kevin on 8/11/17.
 */

public interface LocalizacaoDAO extends GenericDAO<Localizacao, Long>{
    void deletarLocalizacoes();
    void deletarLocalizacoes(long idMonitorado);
    List<Localizacao> findActives();
    List<Localizacao> findByMonitorado(long idMonitorado);
    int deleteByMonitorado(Long idMonitorado);
    List<Localizacao> findAll(long idMonitorado, int periodo, int pontos);
}
