package br.senai.collabtrack.entity;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "monitor_has_monitorado")
@AssociationOverrides({
		@AssociationOverride(name = "primaryKey.monitor", joinColumns = @JoinColumn(name = "id_monitor")),
		@AssociationOverride(name = "primaryKey.monitorado", joinColumns = @JoinColumn(name = "id_monitorado")) })
@DynamicUpdate
public class MonitorMonitorado {

	@EmbeddedId
	private MonitorMonitoradoPK primaryKey = new MonitorMonitoradoPK();
	private boolean principal;
	private boolean ativo;
	@Column(name = "em_monitoramento")
	private boolean emMonitoramento;
	@Column(length = 10)
	private int cor;

	public MonitorMonitoradoPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(MonitorMonitoradoPK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isEmMonitoramento() {
		return emMonitoramento;
	}

	public void setEmMonitoramento(boolean emMonitoramento) {
		this.emMonitoramento = emMonitoramento;
	}

	public int getCor() {
		return cor;
	}

	public void setCor(int cor) {
		this.cor = cor;
	}

	@JsonIgnore
	public Monitor getMonitor() {
		return this.primaryKey.getMonitor();
	}

	public void setMonitor(Monitor monitor) {
		this.primaryKey.setMonitor(monitor);
	}

	@JsonIgnore
	public Monitorado getMonitorado() {
		return this.primaryKey.getMonitorado();
	}

	public void setMonitorado(Monitorado monitorado) {
		this.primaryKey.setMonitorado(monitorado);
	}

}