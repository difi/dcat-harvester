package no.difi.dcat.admin.web.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;

@Controller
@CrossOrigin(origins = "*")
public class UserAdminController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;

	private final Logger logger = LoggerFactory.getLogger(UserAdminController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}
	
	@RequestMapping("/admin/users")
	public ModelAndView viewUsers(Principal principal) {
		String name = principal.getName();
		
		List<UserDto> users = new ArrayList<>();
		
		ModelAndView model = new ModelAndView("users");
		model.addObject("users", users);
	    model.addObject("username", name);
		
		return model;
	}
}
