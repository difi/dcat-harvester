package no.difi.dcat.harvester.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestViewController {

	@RequestMapping("/test")
	public String test(Model model) {
		return "test";
	}	
}
