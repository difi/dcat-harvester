package no.difi.dcat.admin.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.UserNotFoundException;

@Component
public class FusekiUserDetailsService implements UserDetailsService {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(FusekiUserDetailsService.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Map<String,String> userMap;

		if(!adminDataStore.hasAdminUser()){
			createTestUser("test_admin", "password", "ADMIN");
		}

		try {
			userMap = adminDataStore.getUser(username);
			return new User(username, userMap.get("password"), Arrays.asList(new SimpleGrantedAuthority(userMap.get("role"))));
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		
	}
	
	private void createTestUser(String username, String password, String role) {
		try {
			no.difi.dcat.datastore.domain.User user = new no.difi.dcat.datastore.domain.User(null, username, passwordEncoder.encode(password), username+"@example.org", role);
			adminDataStore.addUser(user);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
}
