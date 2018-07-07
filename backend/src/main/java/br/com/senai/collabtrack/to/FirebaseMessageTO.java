package br.com.senai.collabtrack.to;

public class FirebaseMessageTO {

	public FirebaseMessageTO(String to, FirebaseTO data) {
		this.to = to;
		this.data = data;
	}

	private String to;
	private FirebaseTO data;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public FirebaseTO getData() {
		return data;
	}

	public void setData(FirebaseTO data) {
		this.data = data;
	}

}
