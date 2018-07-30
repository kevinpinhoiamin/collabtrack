package br.senai.collabtrack.controller;

import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import br.senai.collabtrack.entity.Mensagem;
import br.senai.collabtrack.exception.CustomException;
import br.senai.collabtrack.service.MensagemService;
import br.senai.collabtrack.to.RespostaTO;

@RestController
@RequestMapping("/mensagem")
public class MensagemController extends GenericController {

	@Autowired
	MensagemService service;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Mensagem>> findByMonitorCelular(@RequestParam("celular") long celular) {
		return ok(service.findByMonitorCelular(celular));
	}

	@RequestMapping(method = RequestMethod.POST, path = "/audio")
	public ResponseEntity<Mensagem> audio(@RequestParam("audio") MultipartFile file, 
											@RequestParam("monitor") long idMonitor, 
											@RequestParam("monitorado") long idMonitorado ) throws CustomException{
		return ok(service.addAudio(file, idMonitor, idMonitorado));
	}	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Mensagem> novaMensagem(@RequestBody Mensagem mensagem) throws CustomException{
		mensagem.setTipo(1);
		return ok(service.save(mensagem));
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/audio/{monitorado}/{audio}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<FileSystemResource> buscarAudio(@PathVariable("monitorado") long monitorado, @PathVariable("audio") long audio) {
		return ok(new FileSystemResource(service.buscarAudio(audio, monitorado)));
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/proximoaudio/{monitorado}")
	public ResponseEntity<Long> proximoDaFila(@PathVariable("monitorado") Long monitorado){
		return ok(service.proximoDaFila(monitorado));
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/resposta")
	public ResponseEntity<Boolean> salvarResposta(@RequestBody RespostaTO resposta) throws CustomException{
		return ok(service.salvarResposta(resposta));
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/aviso")
	public ResponseEntity<Boolean> aviso(@RequestBody RespostaTO resposta) throws CustomException{
		return ok(service.salvarAviso(resposta));
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/areasegura")
	public ResponseEntity<Boolean> areaSegura(@RequestBody RespostaTO aviso) throws CustomException{
		return ok(service.salvarAvisoForaAreaSegura(aviso));
	}
}
