package br.com.senai.collabtrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.collabtrack.service.AreaSeguraService;
import br.com.senai.collabtrack.to.AreaSeguraTO;

@RestController
@RequestMapping("/area-segura")
public class AreaSeguraController extends GenericController {

	@Autowired
	AreaSeguraService service;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<AreaSeguraTO> post(@RequestBody AreaSeguraTO areaSeguraTO) {
		return ok(service.save(areaSeguraTO));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<AreaSeguraTO> update(@RequestBody AreaSeguraTO areaSeguraTO) {
		return ok(service.update(areaSeguraTO));
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@RequestParam("id") Long id, @RequestParam("celular") Long celular) {
		service.delete(id, celular);
		return noContent();
	}

}
