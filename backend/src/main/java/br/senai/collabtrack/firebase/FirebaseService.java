package br.senai.collabtrack.firebase;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.senai.collabtrack.to.FirebaseMessageTO;
import br.senai.collabtrack.to.FirebaseTO;

@Service
public class FirebaseService {
	
	@Value("${firebase.key.monitor}")
	private String keyMonitor;
	
	@Value("${firebase.key.monitorado}")
	private String keyMonitorado;
	
	@Value("${firebase.api}")
	private String firebaseApi;
	
	/**
	 * Método que manda o objeto de transferência do Firebase para o dispositivo de um monitor
	 * @param firebaseTO Instância do objeto que será transferido pelo Firebase
	 * @param firebaseToken Token do dispositivo de destino
	 */
	@Async
	public void sendToMonitor(FirebaseTO firebaseTO, String firebaseToken) {
		send(firebaseTO, firebaseToken, keyMonitor);
	}
	
	/**
	 * Método que manda o objeto de transferência do Firebase para o dispositivo de um monitorado
	 * @param firebaseTO Instância do objeto que será transferido pelo Firebase
	 * @param firebaseToken Token do dispositivo de destino
	 */
	@Async
	public void sendToMonitorado(FirebaseTO firebaseTO, String firebaseToken) {
		send(firebaseTO, firebaseToken, keyMonitorado);
	}
	
	/**
	 * Método que manda o objeto de transferência do Firebase para o dispositivo do monitor ou do monitorado
	 * @param firebaseTO Instância do objeto que será transferido pelo Firebase
	 * @param firebaseToken Token do dispositivo de destino
	 * @param firebaseAuthorizationKey Token de autorização para utilização da API do Firebase
	 */
	private void send(FirebaseTO firebaseTO, String firebaseToken, String firebaseAuthorizationKey) {
		
		if(firebaseTO == null || firebaseToken == null || firebaseToken.isEmpty()) {
			return;
		}
		
		FirebaseMessageTO firebaseMessaTO = new FirebaseMessageTO(firebaseToken, firebaseTO);
		
		// Request interceptor
		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "Key=" + firebaseAuthorizationKey));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		
		// Request
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(interceptors);
		restTemplate.postForObject(firebaseApi, new HttpEntity<FirebaseMessageTO>(firebaseMessaTO), String.class);
		
	}
	
}
