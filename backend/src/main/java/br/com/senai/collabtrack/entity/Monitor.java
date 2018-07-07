package br.com.senai.collabtrack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

@Entity(name = "monitor")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Monitor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 11)
	@Digits(integer = 11, fraction = 0, message = "Celular deve conter 11 caracteres")
	private long celular;
	@Column(nullable = false, length = 60)
	@NotEmpty(message = "Nome é obrigatório")
	@Size(min = 1, max = 60, message = "Nome deve ter entre 1 e 60 caracteres")
	private String nome;
	private String token;
	@Column(name = "token_autenticacao", nullable = false, length = 6)
	@NotEmpty(message = "Token de autenticação é obrigatório")
	@Size(min = 6, max = 6, message = "Token de autenticação deve conter 6 caracteres")
	private String tokenAutenticacao;

	@Transient
	private boolean marcado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getCelular() {
		return celular;
	}

	public void setCelular(long celular) {
		this.celular = celular;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenAutenticacao() {
		return tokenAutenticacao;
	}

	public void setTokenAutenticacao(String tokenAutenticacao) {
		this.tokenAutenticacao = tokenAutenticacao;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

}
