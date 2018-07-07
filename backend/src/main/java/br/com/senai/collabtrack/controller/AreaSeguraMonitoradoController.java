package br.com.senai.collabtrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.collabtrack.entity.AreaSeguraMonitorado;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.service.AreaSeguraMonitoradoService;

@RestController
@RequestMapping("/area-segura-monitorado")
public class AreaSeguraMonitoradoController extends GenericController {

	@Autowired
	AreaSeguraMonitoradoService service;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<AreaSeguraMonitorado>> get(@RequestParam("celular") long celular) {
		return ok(service.find(celular));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<AreaSeguraMonitorado> update(@RequestBody AreaSeguraMonitorado areaSeguraMonitorado)
			throws CustomException {
		return ok(service.update(areaSeguraMonitorado));
	}

}
