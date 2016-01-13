package no.difi.dcat.admin.web.user;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.User;

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
	public ModelAndView viewUsers(@RequestParam(value="edit", required=false) String editUsername, Principal principal) {
		String name = principal.getName();
		
		List<User> users = adminDataStore.getUsers();

		ModelAndView model = new ModelAndView("users");
		model.addObject("users", users);
	    model.addObject("username", name);
		
	    if (editUsername != null) {
	    	logger.trace("Looking for username to edit {}", editUsername);
	    	Optional<User> editUser = users.stream().filter((User user) -> user.getUsername().equalsIgnoreCase(editUsername)).findFirst();
	    	if (editUser.isPresent()) {
	    		logger.trace("User found {}", editUsername);
	    		model.addObject("editUser", editUser.get());
	    	}
	    }
	    
		return model;
	}
}
