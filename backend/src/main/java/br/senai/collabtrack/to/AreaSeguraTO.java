package br.senai.collabtrack.to;

import java.util.List;

import javax.validation.Valid;

import br.senai.collabtrack.entity.AreaSegura;
import br.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.senai.collabtrack.entity.Monitor;

public class AreaSeguraTO {

	private Monitor monitor;
	private @Valid AreaSegura areaSegura;
	private List<AreaSeguraMonitorado> areaSeguraMonitorados;

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public AreaSegura getAreaSegura() {
		return areaSegura;
	}

	public void setAreaSegura(AreaSegura areaSegura) {
		this.areaSegura = areaSegura;
	}

	public List<AreaSeguraMonitorado> getAreaSeguraMonitorados() {
		return areaSeguraMonitorados;
	}

	public void setAreaSeguraMonitorados(List<AreaSeguraMonitorado> areaSeguraMonitorados) {
		this.areaSeguraMonitorados = areaSeguraMonitorados;
	}

}
