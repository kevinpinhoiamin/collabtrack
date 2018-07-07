package br.com.senai.collabtrack.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Mapeia as exceções do Hibernate validator quando a anotação @Valid é
	 * utilizada
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
		List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
		String error;
		for (FieldError fieldError : fieldErrors) {
			// error = fieldError.getField() + ", " + fieldError.getDefaultMessage();
			error = fieldError.getDefaultMessage();
			errors.add(error);
		}
		for (ObjectError objectError : globalErrors) {
			error = objectError.getObjectName() + ", " + objectError.getDefaultMessage();
			errors.add(error);
		}

		return new ResponseEntity<>(new ErrorResponseEntity(errors), headers, status);

	}

	/**
	 * Mapeaia as exceções do Hibernate validator no momento da persistência do
	 * objeto
	 * 
	 * @param exception
	 * @param webRequest
	 * @return
	 */

	@ExceptionHandler({ ConstraintViolationException.class })
	protected ResponseEntity<ErrorResponseEntity> handleConstraintViolation(RuntimeException exception, WebRequest webRequest) {
		
		ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
		Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();

		List<String> errors = new ArrayList<>(violations.size());
		for (ConstraintViolation<?> violation : violations) {
			// cv.getPropertyPath().toString() + ", " + cv.getMessage()
			errors.add(violation.getMessage());
		}

		return new ResponseEntity<ErrorResponseEntity>(new ErrorResponseEntity(errors), HttpStatus.BAD_REQUEST);

	}
	
	@ExceptionHandler({ CustomException.class })
	protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException exception) {
		return new ResponseEntity<ErrorResponseEntity>(new ErrorResponseEntity(exception.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	
	/*
	 * Mapeia exceção de transação (rollback, por exemplo)
	@ExceptionHandler(value = {TransactionSystemException.class})
    public ResponseEntity<ErrorResponseEntity> handleTxException(TransactionSystemException ex) {
		
		List<String> errors = new ArrayList<>();

        Throwable t = ex.getCause();
        if(t instanceof ConstraintViolationException){
            ConstraintViolationException cve = (ConstraintViolationException)t;
            Set violations = cve.getConstraintViolations();
            ConstraintViolation v = (ConstraintViolation) violations.toArray()[0];
            errors.add(v.getMessage());
        }else {
        	errors.add(ex.getMessage());
        }
        return new ResponseEntity(new ErrorResponseEntity(errors), HttpStatus.BAD_REQUEST);
	}
	*/
	
}
