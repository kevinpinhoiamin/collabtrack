package br.senai.collabtrack.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class AreaSeguraMonitoradoPK implements Serializable {

	public AreaSeguraMonitoradoPK() {

	}

	public AreaSeguraMonitoradoPK(AreaSegura areaSegura, Monitorado monitorado) {
		this.areaSegura = areaSegura;
		this.monitorado = monitorado;
	}

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private AreaSegura areaSegura;
	@ManyToOne
	private Monitorado monitorado;

	public AreaSegura getAreaSegura() {
		return areaSegura;
	}

	public void setAreaSegura(AreaSegura areaSegura) {
		this.areaSegura = areaSegura;
	}

	public Monitorado getMonitorado() {
		return monitorado;
	}

	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

}
