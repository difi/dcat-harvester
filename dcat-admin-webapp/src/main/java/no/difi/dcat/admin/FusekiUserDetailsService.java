package no.difi.dcat.admin;

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
		
		logger.info("Setting up test users: test_user, test_admin"); //TODO: bør legges i fuseki
		if (username.equalsIgnoreCase("test_user")) {
			userMap.put("username", "test_user");
			userMap.put("password", passwordEncoder.encode("passord"));
			userMap.put("role", "USER");
		} else if (username.equalsIgnoreCase("test_admin")) {
			userMap.put("username", "test_admin");
			userMap.put("password", passwordEncoder.encode("passord"));
			userMap.put("role", "ADMIN");
		} else {
			adminDataStore.getUser(username);
		}
		
		if (!userMap.containsKey("username")) {
			throw new UsernameNotFoundException("Not such username: " + username);
		} else {
			return new User(username, userMap.get("password"), Arrays.asList(new SimpleGrantedAuthority(userMap.get("role"))));
		}
	}

}
