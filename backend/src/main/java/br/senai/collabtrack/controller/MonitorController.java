package br.senai.collabtrack.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.senai.collabtrack.entity.Monitor;
import br.senai.collabtrack.exception.CustomException;
import br.senai.collabtrack.service.MonitorService;
import br.senai.collabtrack.util.TokenUtil;

@RestController
@RequestMapping("/monitor")
public class MonitorController extends GenericController {

	@Autowired
	MonitorService service;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Monitor> save(@RequestBody Monitor monitor) throws CustomException {
		monitor.setTokenAutenticacao(new TokenUtil().getToken());
		return ok(service.save(monitor));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Monitor> update(@RequestBody @Valid Monitor monitor) throws CustomException {
		return ok(service.update(monitor));
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/picture", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> updatePicture(@RequestParam("id_monitor") long idMonitor,
			@RequestParam("file_name") String fileName, @RequestParam("base64") String base64) throws CustomException {
		service.updatePicture(idMonitor, fileName, base64);
		return noContent();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Monitor>> findAll() {
		return ok(service.findAll());
	}

	@RequestMapping(method = RequestMethod.GET, path = "/picture/{celular}")
	public ResponseEntity<FileSystemResource> findPicture(@PathVariable("celular") long celular)
			throws CustomException, IOException {
		return ok(new FileSystemResource(service.findPicture(celular)));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/celular/{celular}")
	public ResponseEntity<Monitor> get(@PathVariable("celular") long celular) throws CustomException {
		return ok(service.find(celular));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public ResponseEntity<Monitor> login(@RequestParam("celular") long celular,
			@RequestParam("token") String tokenAutenticacao) throws CustomException {
		return ok(service.find(celular, tokenAutenticacao));
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/logout")
	public ResponseEntity<Monitor> logout(@RequestBody @Valid Monitor monitor) throws CustomException {
		return ok(service.logout(monitor));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/monitorado")
	public ResponseEntity<Monitor> get(@RequestParam("celular_monitorado") long celularMonitorado,
			@RequestParam("token_autenticacao") String tokenAutenticacao) {
		return ok(service.findByMonitoradoCelularAndTokenAutenticacao(celularMonitorado, tokenAutenticacao));
	}

	@RequestMapping(method = RequestMethod.GET, path = "monitores")
	public ResponseEntity<List<Monitor>> get(@RequestParam("id-monitor") long idMonitor,
			@RequestParam("id-monitorado") long idMonitorado) {
		return ok(service.findOthersRelated(idMonitor, idMonitorado));
	}

}
