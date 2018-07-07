package br.com.senai.collabtrack.jpa;

import java.util.List;

import br.com.senai.collabtrack.entity.Mensagem;

public interface MensagemRepository {

	List<Mensagem> findByMonitorCelular(long celular);
	Mensagem findFirstByMonitoradoIdAndTipo(long idMonitorado, int tipo);
	Mensagem findFirstByMonitoradoId(long idMonitorado);
	Mensagem proximoAudioDaFila(long monitorado);
	Mensagem ultimaMensagemAreaSegura(long id);
	
}
