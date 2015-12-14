package no.difi.dcat.harvester.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

	@RequestMapping("/api/test")
	public String test() {
		return "Test OK!";
	}
}