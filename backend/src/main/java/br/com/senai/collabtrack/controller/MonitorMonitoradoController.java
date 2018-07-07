package br.com.senai.collabtrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.collabtrack.entity.MonitorMonitorado;
import br.com.senai.collabtrack.exception.CustomException;
import br.com.senai.collabtrack.service.MonitorMonitoradoService;

@RestController
@RequestMapping("/monitor-monitorado")
public class MonitorMonitoradoController extends GenericController {

	@Autowired
	MonitorMonitoradoService service;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<MonitorMonitorado>> findByMonitorId(@RequestParam("id-monitor") long idMonitor) {
		return ok(service.findByMonitorId(idMonitor));
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<MonitorMonitorado> update(@RequestBody MonitorMonitorado monitorMonitorado) throws CustomException {
		return ok(service.update(monitorMonitorado));
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/monitoramento")
	public ResponseEntity<?> monitorar(@RequestBody MonitorMonitorado monitorMonitorado) throws CustomException {
		service.monitorar(monitorMonitorado);
		return noContent();
	}

}
