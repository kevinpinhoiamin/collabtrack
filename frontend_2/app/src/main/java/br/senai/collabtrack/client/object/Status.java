package br.senai.collabtrack.client.object;

public class Status {

	private long id;
	private boolean wifi;
	private boolean internetMovel;
	private boolean localizacao;
	private int bateria;
	private Monitorado monitorado;

	public Status(Monitorado monitorado){
		this.monitorado = monitorado;
	}

	public Status(){}

	@Override
	public String toString() {
		return String.format("{\"wifi\":%s, \"internetMovel\":%s, \"localizacao\":%s, \"bateria\":%s, \"monitorado\":%s}", wifi, internetMovel, localizacao, bateria, monitorado);
	}

	public long getId() {
		return id;
	}
	public synchronized void setId(long id) {
		this.id = id;
	}
	public boolean isWifi() {
		return wifi;
	}
	public synchronized void setWifi(boolean wifi) {
		this.wifi = wifi;
	}
	public boolean isInternetMovel() {
		return internetMovel;
	}
	public synchronized void setInternetMovel(boolean internetMovel) {
		this.internetMovel = internetMovel;
	}
	public boolean isLocalizacao() {
		return localizacao;
	}
	public synchronized void setLocalizacao(boolean localizacao) {
		this.localizacao = localizacao;
	}
	public int getBateria() {
		return bateria;
	}
	public synchronized void setBateria(int bateria) {
		this.bateria = bateria;
	}
	public Monitorado getMonitorado() {
		return monitorado;
	}
	public void setMonitorado(Monitorado monitorado) {
		this.monitorado = monitorado;
	}
		
}
