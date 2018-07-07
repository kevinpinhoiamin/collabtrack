package br.com.senai.collabtrack.controller;

import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hibernate")
public class HibernateController extends GenericController{

	@Autowired
	Statistics statistics;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Statistics> get() {
		return ok(statistics);
	}
	
}
