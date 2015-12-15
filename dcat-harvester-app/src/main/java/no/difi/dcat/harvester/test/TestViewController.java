package no.difi.dcat.harvester.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import no.difi.dcat.harvester.settings.TestSettings;

@Controller
public class TestViewController {

	@Autowired
	private TestSettings applicationSettings;
	
	@RequestMapping("/test")
	public String test(Model model) {
		return "test";
	}	
}
