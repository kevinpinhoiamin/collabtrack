package br.com.senai.collabtrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenericController {
	
	/**
	 * Método que faz o build de um objeto de Response
	 * @param result Objeto que será retornado como resultado
	 * @return Instância do Response com o status 200 (OK)
	 */
	protected <T> ResponseEntity<T> ok(T object) {		
		return new ResponseEntity<T>(object, HttpStatus.OK);
	}
	
	/**
	 * Método que faz o build de um objeto de Response
	 * @param str Mensagem que será enviada através do Response
	 * @return Instância do objeto Response com o status 500 (erro interno no servidor)
	 */
	protected <T> ResponseEntity<T> badRequest(T object) {
		return new ResponseEntity<T>(object, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Método que faz o build de um objeto de Response
	 * @return Instância do objeto Response com o status 204 (sem conteudo)
	 */
	protected ResponseEntity<?> noContent() {
		return ResponseEntity.noContent().build();
	}
	
}
