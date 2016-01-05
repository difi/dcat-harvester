package no.difi.dcat.admin.web;

import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import no.difi.dcat.admin.settings.ApplicationSettings;
import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;

@Controller
@CrossOrigin(origins = "*")
public class AdminController {

	@Autowired
	private FusekiSettings fusekiSettings;
	@Autowired
	private ApplicationSettings applicationSettings;
	private AdminDataStore adminDataStore;

	private final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}

	@RequestMapping("/admin")
	public ModelAndView viewAllDcatSources(Principal principal) {
		String name = principal.getName();
		
		List<DcatSource> dcatSources;
		if (name.equalsIgnoreCase("admin")) {
			dcatSources = adminDataStore.getDcatSources();
		} else {
			dcatSources = adminDataStore.getDcatSourcesForUser(name);
		}
		
		ModelAndView model = new ModelAndView("admin");
		model.addObject("dcatSources", dcatSources);
		model.addObject("dcatSource", new DcatSource());
	    model.addObject("username", name);
		
		return model;
	}
	
	@RequestMapping(value= "/admin/addDcatSource", method = RequestMethod.POST)
	public ModelAndView addDcatSource(@Valid @ModelAttribute("dcatSource") DcatSource dcatSource, ModelMap model, Principal principal) {

		dcatSource.setName(String.format("http://dcat.difi.no/%s", UUID.randomUUID().toString()));
		dcatSource.setUser(principal.getName());
		
		adminDataStore.addDcatSource(dcatSource);
		
		model.clear();
		return new ModelAndView("redirect:/admin");
	}
	
	@RequestMapping(value= "/admin/deleteDcatSource", method = RequestMethod.GET)
	public ModelAndView deleteDcatSource(@RequestParam("name") String dcatSourceName, ModelMap model) {
		adminDataStore.deleteDcatSource(dcatSourceName);
		
		model.clear();
		return new ModelAndView("redirect:/admin");
	}
	
	@RequestMapping(value= "/admin/harvestDcatSource", method = RequestMethod.GET)
	public ModelAndView harvestDcatSource(@RequestParam("name") String dcatSourceName, ModelMap model) {
		try {
			URL url = new URL(applicationSettings.getHarvesterUrl() + "/api/admin/harvest?name=" + dcatSourceName);
			url.openConnection().getInputStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.clear();
		return new ModelAndView("redirect:/admin");
	}
}


