package br.com.senai.colabtrack.dao;

import br.com.senai.colabtrack.domain.Mensagem;

/**
 * Created by Lucas Matheus Nunes on 31/07/2017.
 */

public interface MensagemDAO extends GenericDAO<Mensagem, Long>{
    void updadeMensagem(long id, String data);
    Long updateId(Mensagem mensagem, Long id);
    int deleteByMonitorado(Long idMonitorado);
    Mensagem findUltimaMensagem(Long idMonitorado);
}
