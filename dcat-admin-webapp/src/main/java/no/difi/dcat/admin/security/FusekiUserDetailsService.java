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

		Map<String,String> userMap = new HashMap<>();
		
		try {
			if (username.equalsIgnoreCase("test_user")) {
				userMap = getTestUser("test_user", "password", "USER");
			} else if (username.equalsIgnoreCase("test_admin")) {
				userMap = getTestUser("test_admin", "password", "ADMIN");
			} else {
				userMap = adminDataStore.getUser(username);
			}
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		
		return new User(username, userMap.get("password"), Arrays.asList(new SimpleGrantedAuthority(userMap.get("role"))));
	}
	
	private Map<String,String> getTestUser(String username, String password, String role) {
		Map<String,String> userMap = new HashMap<>();
		userMap.put("username", username);
		userMap.put("password", passwordEncoder.encode(password));
		userMap.put("role", role);
		return userMap;
	}

}
