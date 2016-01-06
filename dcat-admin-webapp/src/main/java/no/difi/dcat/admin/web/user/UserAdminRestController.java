package no.difi.dcat.admin.web.user;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;

@RestController
@CrossOrigin(origins = "*")
public class UserAdminRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(UserAdminRestController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		
		logger.debug("Adding test users");
		adminDataStore.addUser("user", passwordEncoder.encode("password"), "USER");
		adminDataStore.addUser("admin", passwordEncoder.encode("password"), "ADMIN");
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.POST)
	public void addUser(@Valid @RequestBody UserDto userDto) {
		adminDataStore.addUser(userDto.getUsername(), userDto.getPassword(), userDto.getRole());
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.DELETE)
	public void deleteUser(@Valid @RequestParam("delete") String username) {
		adminDataStore.deleteUser(username);
	}
	
}
