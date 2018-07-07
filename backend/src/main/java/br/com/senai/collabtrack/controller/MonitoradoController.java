package br.com.senai.collabtrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.collabtrack.entity.Monitorado;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.service.MonitoradoService;
import br.com.senai.collabtrack.to.MonitoradoTO;

@RestController
@RequestMapping("/monitorado")
public class MonitoradoController extends GenericController {

	@Autowired
	MonitoradoService service;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<MonitoradoTO> post(@RequestBody MonitoradoTO monitoradoTO) throws CustomException {
		return ok(service.save(monitoradoTO));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<MonitoradoTO> put(@RequestBody MonitoradoTO monitoradoTO) throws CustomException {
		;
		return ok(service.save(monitoradoTO));
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		service.delete(id);
		return noContent();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Iterable<Monitorado>> get() {
		return ok(service.findAll());
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/{celular}")
	public ResponseEntity<Monitorado> findByCelular(@PathVariable("celular") long celular) {
		return ok(service.findFirstByCelular(celular));
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/monitores/{id}")
	public ResponseEntity<MonitoradoTO> get(@PathVariable("id") long idMonitorado) {
		return ok(service.findByMonitoradoId(idMonitorado));
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/token/{id}/{token}")
	public ResponseEntity<Monitorado> update(@PathVariable long id, @PathVariable String token) throws CustomException {
		return ok(service.updateToken(id, token));
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/em-monitoramento/{id}")
	public ResponseEntity<Boolean> isEmMonitoramento(@PathVariable("id") long idMonitorado) {
		return ok(service.isEmMonitoramento(idMonitorado));
	}

}
