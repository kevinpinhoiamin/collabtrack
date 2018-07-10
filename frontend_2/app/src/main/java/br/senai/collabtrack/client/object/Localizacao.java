package br.senai.collabtrack.client.object;

public class Localizacao {

	private Long id;
	private double latitude;
	private double longitude;
	private Monitorado monitorado;

	public Localizacao(Monitorado monitorado){
		this.monitorado = monitorado;
	}

	@Override
	public String toString() {
		return String.format("{\"latitude\":%s, \"longitude\":%s, \"monitorado\":%s}", latitude, longitude, monitorado);
	}

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
	public Monitorado getMonitorado() {
		return monitorado;
	}
	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}

}
