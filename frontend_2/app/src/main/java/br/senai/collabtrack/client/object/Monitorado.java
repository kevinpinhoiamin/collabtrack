package br.senai.collabtrack.client.object;

public class Monitorado {

	private Long id;
	private Long numberCel;
	private Long idMonitor;
	private Long numberCelMonitor;

	public Monitorado(){ }

	public Monitorado(Long id){
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("{\"id\":%s, \"numberCel\":%s}", id, numberCel);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getCelular() {
		return numberCel;
	}
	public void setCelular(Long celular) {
		this.numberCel = celular;
	}

	public Long getIdMonitor() {
		return idMonitor;
	}
	public void setIdMonitor(Long idMonitor) {
		this.idMonitor = idMonitor;
	}

	public Long getCelularMonitor() {
		return numberCel;
	}
	public void setCelularMonitor(Long celular) {
		this.numberCel = celular;
	}

}
