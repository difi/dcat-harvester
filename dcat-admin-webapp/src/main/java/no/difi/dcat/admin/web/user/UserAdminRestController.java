package no.difi.dcat.admin.web.user;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import no.difi.dcat.datastore.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	private final Logger logger = LoggerFactory.getLogger(UserAdminRestController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.POST)
	public void addUser(@Valid @RequestBody UserDto userDto) {
		try {
			adminDataStore.addUser(userDto.getUsername(), userDto.getPassword(), userDto.getRole());
		} catch (UserAlreadyExistsException e) {
			//@TODO Do something with this exception
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.DELETE)
	public void deleteUser(@Valid @RequestParam("delete") String username) {
		adminDataStore.deleteUser(username);
	}
	
}
