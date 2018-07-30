package br.senai.collabtrack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

@Entity(name = "area_segura")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AreaSegura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 40)
	@NotEmpty(message = "Nome é obrigatório")
	@Size(min = 1, max = 40, message = "Nome deve ter entre 1 e 40 caracteres")
	private String nome;
	@Column(nullable = false, length = 10)
	private int cor;
	@Column(name = "cor_borda", length = 10)
	private int corBorda;
	@Column(length = 4)
	@Min(value = 100, message = "Raio não pode ser menor que 100 metros")
	@Max(value = 1000, message = "Raio não pode ultrapassar 1000 metros")
	private int raio;
	@Min(value = -90, message = "Latitude não pode ser menor que -90")
	@Max(value = 90, message = "Latitude não pode ser maior que 90")
	private double latitude;
	@Min(value = -180, message = "Longitude não pode ser menor que -180")
	@Max(value = 180, message = "Longitude não pode ser maior que 180")
	private double longitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getCor() {
		return cor;
	}

	public void setCor(int cor) {
		this.cor = cor;
	}

	public int getCorBorda() {
		return corBorda;
	}

	public void setCorBorda(int corBorda) {
		this.corBorda = corBorda;
	}

	public int getRaio() {
		return raio;
	}

	public void setRaio(int raio) {
		this.raio = raio;
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

}
