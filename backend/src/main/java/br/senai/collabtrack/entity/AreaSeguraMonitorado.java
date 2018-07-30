package br.senai.collabtrack.entity;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "area_segura_has_monitorado")
@AssociationOverrides({
		@AssociationOverride(name = "primaryKey.areaSegura", joinColumns = @JoinColumn(name = "id_area_segura")),
		@AssociationOverride(name = "primaryKey.monitorado", joinColumns = @JoinColumn(name = "id_monitorado")) })
@DynamicUpdate
public class AreaSeguraMonitorado {

	@EmbeddedId
	private AreaSeguraMonitoradoPK primaryKey = new AreaSeguraMonitoradoPK();
	private boolean ativa;

	public AreaSeguraMonitoradoPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(AreaSeguraMonitoradoPK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	@JsonIgnore
	public AreaSegura getAreaSegura() {
		return this.primaryKey.getAreaSegura();
	}

	@JsonIgnore
	public void setAreaSegura(AreaSegura areaSegura) {
		this.primaryKey.setAreaSegura(areaSegura);
	}

	@JsonIgnore
	public Monitorado getMonitorado() {
		return this.primaryKey.getMonitorado();
	}

	@JsonIgnore
	public void setMonitorado(Monitorado monitorado) {
		this.primaryKey.setMonitorado(monitorado);
	}

}
