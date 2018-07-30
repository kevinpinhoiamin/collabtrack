package br.senai.collabtrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.senai.collabtrack.entity.Localizacao;
import br.senai.collabtrack.service.LocalizacaoService;

@RestController
@RequestMapping("/localizacao")
public class LocalizacaoController extends GenericController {

	@Autowired
	LocalizacaoService service;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Localizacao> save(@RequestBody Localizacao localizacao) {
		return ok(service.save(localizacao));
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Localizacao>> get(@RequestParam(value = "monitorado", required=false) long[] monitorados,
			@RequestParam("periodo") int periodo, @RequestParam("pontos") int pontos) {
		return ok(service.search(monitorados, periodo, pontos));
	}

}
