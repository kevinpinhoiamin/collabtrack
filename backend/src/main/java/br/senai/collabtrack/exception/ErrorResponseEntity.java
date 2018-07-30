package br.senai.collabtrack.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ErrorResponseEntity {

	private List<String> errors;

	public ErrorResponseEntity() {
	}

	public ErrorResponseEntity(List<String> errors) {
		this.errors = errors;
	}

	public ErrorResponseEntity(String error) {
		this(Collections.singletonList(error));
	}

	public ErrorResponseEntity(String... errors) {
		this(Arrays.asList(errors));
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}