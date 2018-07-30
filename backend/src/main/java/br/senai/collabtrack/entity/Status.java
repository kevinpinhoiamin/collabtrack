package br.senai.collabtrack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "status")
@DynamicUpdate
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	private Long id;
	@Column(nullable = false)
	private boolean wifi;
	@Column(name = "internet_movel", nullable = false)
	private boolean internetMovel;
	@Column(name = "localizacao", nullable = false)
	private boolean localizacao;
	@Column(nullable = false, columnDefinition = "TINYINT")
	@Min(value = 0, message = "Bateria não pode ser menor que 0%")
	@Max(value = 100, message = "Bateria não pode ser maior que 100%")
	private int bateria;
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt-BR", timezone = "Brazil/East")
	private Date data;
	@OneToOne
	@JoinColumn(name = "id_monitorado")
	private Monitorado monitorado;

	public Status() {

	}

	public Status(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isWifi() {
		return wifi;
	}

	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}

	public boolean isInternetMovel() {
		return internetMovel;
	}

	public void setInternetMovel(boolean internetMovel) {
		this.internetMovel = internetMovel;
	}

	public boolean isLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(boolean localizacao) {
		this.localizacao = localizacao;
	}

	public int getBateria() {
		return bateria;
	}

	public void setBateria(int bateria) {
		this.bateria = bateria;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Monitorado getMonitorado() {
		return monitorado;
	}

	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

}
