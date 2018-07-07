package br.com.senai.collabtrack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "mensagem")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Mensagem {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;
	@Lob
	private String mensagem;
	private int tipo;
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt-BR", timezone = "Brazil/East")
	private Date data;
	@Column(name = "id_resposta")
	private int resposta;
	private boolean respondido;

	@ManyToOne
	@JoinColumn(name = "id_monitorado")
	private Monitorado monitorado;

	@ManyToOne
	@JoinColumn(name = "id_monitor")
	private Monitor monitor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getResposta() {
		return resposta;
	}

	public void setResposta(int resposta) {
		this.resposta = resposta;
	}

	public boolean isRespondido() {
		return respondido;
	}

	public void setRespondido(boolean respondido) {
		this.respondido = respondido;
	}

	public Monitorado getMonitorado() {
		return monitorado;
	}

	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

}
