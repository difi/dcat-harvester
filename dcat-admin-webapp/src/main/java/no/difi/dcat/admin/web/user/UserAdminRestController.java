package no.difi.dcat.admin.web.user;

import java.security.Principal;

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
import no.difi.dcat.datastore.AdminDcatDataService;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.UserAlreadyExistsException;
import no.difi.dcat.datastore.domain.User;

@RestController
@CrossOrigin(origins = "*")
public class UserAdminRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	private AdminDcatDataService adminDcatDataService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(UserAdminRestController.class);
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		adminDcatDataService = new AdminDcatDataService(adminDataStore, new DcatDataStore(new Fuseki(fusekiSettings.getAdminServiceUri())));
		
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.POST)
	public void addUser(@Valid @RequestBody UserDto userDto) {
		try {
			User user = convertToDomain(userDto);
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			adminDataStore.addUser(user);
		} catch (UserAlreadyExistsException e) {
			//@TODO Do something with this exception
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/admin/user", method = RequestMethod.DELETE)
	public void deleteUser(@Valid @RequestParam("delete") String username, Principal principal) {
		if (principal == null) {
			return;
		}
		adminDcatDataService.deleteUser(username, adminDataStore.getUserObject(principal.getName()));
	}
	
	private static User convertToDomain(UserDto dto) {
		return new User(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getEmail(), dto.getRole());
	}
	
	private static UserDto convertToDto(User user) {
		return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
	}
	
}
