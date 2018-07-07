package br.com.senai.collabtrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.collabtrack.entity.Status;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.service.StatusService;

@RestController
@RequestMapping("/status")
public class StatusController extends GenericController {

	@Autowired
	StatusService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Status>> findByMonitorCelular(@RequestParam("celular") long celular) {
		return ok(service.findByMonitorCelular(celular));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Status> save(@RequestBody Status status) throws CustomException {
		return ok(service.save(status, true));
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Status> update(@RequestBody Status status) throws CustomException {
		return ok(service.save(status, true));
	}
	
}
