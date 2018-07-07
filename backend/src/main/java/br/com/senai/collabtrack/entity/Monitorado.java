package br.com.senai.collabtrack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

@Entity(name = "monitorado")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Monitorado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 60)
	@NotEmpty(message = "Nome é obrigatório")
	@Size(min = 1, max = 60, message = "Nome deve ter entre 1 e 60 caracteres")
	private String nome;
	@Column(nullable = false, length = 11)
	@Digits(integer=11, fraction=0, message = "Celular deve conter 11 caracteres")
	private long celular;
	private String token;

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

	public long getCelular() {
		return celular;
	}

	public void setCelular(long celular) {
		this.celular = celular;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
