package no.difi.dcat.admin.web.dcat;

import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import no.difi.dcat.admin.settings.ApplicationSettings;
import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;

@Controller
@CrossOrigin(origins = "*")
public class DcatAdminController {

	@Autowired
	private FusekiSettings fusekiSettings;
	@Autowired
	private ApplicationSettings applicationSettings;
	private AdminDataStore adminDataStore;

	private final Logger logger = LoggerFactory.getLogger(DcatAdminController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}
	

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("redirect:/admin");
	}

	@RequestMapping("/admin")
	public ModelAndView viewAllDcatSources(@RequestParam(value="edit", required=false) String editDcatSourceName, Principal principal) {
		String name = principal.getName();
		
		List<DcatSource> dcatSources;
		if (name.equalsIgnoreCase("admin")) {
			dcatSources = adminDataStore.getDcatSources();
		} else {
			dcatSources = adminDataStore.getDcatSourcesForUser(name);
		}
		
		ModelAndView model = new ModelAndView("admin");
		model.addObject("dcatSources", dcatSources);
	    model.addObject("username", name);
		
	    if (editDcatSourceName != null) {
	    	logger.trace("Looking for dcat source name to edit {}", editDcatSourceName);
	    	Optional<DcatSource> editDcatSource = dcatSources.stream().filter((DcatSource dcatSource) -> dcatSource.getId().equalsIgnoreCase(editDcatSourceName)).findFirst();
	    	if (editDcatSource.isPresent()) {
	    		logger.trace("Dcat source found {}", editDcatSourceName);
	    		model.addObject("editDcatSource", editDcatSource.get());
	    	}
	    }
	    
		return model;
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


