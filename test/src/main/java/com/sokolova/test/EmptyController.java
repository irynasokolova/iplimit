package com.sokolova.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmptyController {

	@GetMapping("/")
	public ResponseEntity<Void> getHi() {
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
