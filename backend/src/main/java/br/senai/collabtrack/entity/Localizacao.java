package br.senai.collabtrack.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "localizacao")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Localizacao {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;
	@Min(value = -90, message = "Latitude não pode ser menor que -90")
	@Max(value = 90, message = "Latitude não pode ser maior que 90")
	private double latitude;
	@Min(value = -180, message = "Longitude não pode ser menor que -180")
	@Max(value = 180, message = "Longitude não pode ser maior que 180")
	private double longitude;
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt-BR", timezone = "Brazil/East")
	@NotNull(message = "Data é obrigatória")
	private Date data;
	@ManyToOne
	@JoinColumn(name = "id_monitorado")
	private Monitorado monitorado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
