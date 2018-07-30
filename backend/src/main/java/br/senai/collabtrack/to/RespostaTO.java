package br.senai.collabtrack.to;

public class RespostaTO {
	private long id;
	private boolean resposta;
	private int idMonitor;
	private String data;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id= id;
	}
	public boolean isResposta() {
		return resposta;
	}
	public void setResposta(boolean resposta) {
		this.resposta = resposta;
	}
	public int getIdMonitor() {
		return idMonitor;
	}
	public void setIdMonitor(int idMonitor) {
		this.idMonitor = idMonitor;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
