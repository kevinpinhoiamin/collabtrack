package br.com.senai.collabtrack.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class MonitorMonitoradoPK implements Serializable {

	public MonitorMonitoradoPK() {

	}

	public MonitorMonitoradoPK(Monitor monitor, Monitorado monitorado) {
		this.monitor = monitor;
		this.monitorado = monitorado;
	}
	
	public MonitorMonitoradoPK(long idMonitor, long idMonitorado) {
		this.monitor = new Monitor();
		this.monitor.setId(idMonitor);
		this.monitorado = new Monitorado();
		this.monitorado.setId(idMonitorado);
	}

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private Monitor monitor;
	@ManyToOne
	private Monitorado monitorado;

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public Monitorado getMonitorado() {
		return monitorado;
	}

	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

}