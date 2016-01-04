package no.difi.dcat.admin.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.admin.settings.FusekiSettings;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;

@RestController
@CrossOrigin(origins = "*")
public class AdminRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(AdminRestController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		
		logger.debug("Adding test users");
		adminDataStore.addUser("user", "password", "USER");
		adminDataStore.addUser("admin", "password", "ADMIN");
	}

	@RequestMapping("/api/admin/dcat-sources")
	public ResponseEntity<List<DcatSourceDto>> getDataSources() {
		return new ResponseEntity<List<DcatSourceDto>>(
				adminDataStore.getDcatSources().stream()
				.map((DcatSource dcatSource) -> convertToDto(dcatSource))
				.collect(Collectors.toList()), 
				HttpStatus.OK);
	}

	@RequestMapping(value = "/api/admin/dcat-source", method = RequestMethod.POST)
	public void addDataSource(@Valid @RequestBody DcatSourceDto dcatSourceDto) {
		adminDataStore.addDcatSource(convertToDomain(dcatSourceDto));
	}

	@RequestMapping(value = "/api/admin/dcat-source", method = RequestMethod.DELETE)
	public void deleteDataSource(@Valid @RequestParam("delete") String dcatName) {
		adminDataStore.deleteDcatSource(dcatName);
	}
	
	private DcatSource convertToDomain(DcatSourceDto dto) {
		return new DcatSource(dto.getName(), dto.getDescription(), dto.getUrl(), dto.getUser());
	}
	
	private DcatSourceDto convertToDto(DcatSource domain) {
		return new DcatSourceDto(domain.getName(), domain.getDescription(), domain.getUrl(), domain.getUser());
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
