package com.example.HelseID.Samples.Java.API;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api")
@CrossOrigin(origins = "*")

public class Controller {
	@GetMapping(value = "/public")
	public String publicEndpoint() {
		return "This is public data accessible by everyone";
	}

	@GetMapping(value = "/private")
	public String helloWorld() {
		return "This data is protected by HelseID access token";
	}

	@GetMapping(value = "/private-scoped")
	public String privateScopedEndpoint() {
		return "This data is protected by HelseID access token with the scope: norsk-helsenett:java-sample-api/read";
	}
}
