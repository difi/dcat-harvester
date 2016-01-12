package no.difi.dcat.admin.web.dcat;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import no.difi.dcat.datastore.*;
import no.difi.dcat.datastore.domain.DcatSource;

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

@RestController
@CrossOrigin(origins = "*")
public class DcatAdminRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	private AdminDcatDataService adminDcatDataService;

	private final Logger logger = LoggerFactory.getLogger(DcatAdminController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		adminDcatDataService = new AdminDcatDataService(adminDataStore, new DcatDataStore(new Fuseki(fusekiSettings.getAdminServiceUri())));
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
	public ResponseEntity<String> addDataSource(@Valid @RequestBody DcatSourceDto dcatSourceDto) {
		DcatSource dcatSource = convertToDomain(dcatSourceDto);
		if (dcatSource.getId() == null || dcatSource.getId().isEmpty()) {
			dcatSource.setId(String.format("http://dcat.difi.no/%s", UUID.randomUUID().toString()));
		}
		
		adminDataStore.addDcatSource(dcatSource);
		
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/api/admin/dcat-source", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteDataSource(@Valid @RequestParam("delete") String dcatName, Principal principal) throws UserNotFoundException {
		if (principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		try {
			adminDcatDataService.deleteDcatSource(dcatName, adminDataStore.getUserObject(principal.getName()));
		} catch (UserNotFoundException e) {
			throw e;
		}
		
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
	private DcatSource convertToDomain(DcatSourceDto dto) {
		return new DcatSource(dto.getId(), dto.getDescription(), dto.getUrl(), dto.getUser());
	}
	
	private DcatSourceDto convertToDto(DcatSource domain) {
		return new DcatSourceDto(domain.getId(), domain.getDescription(), domain.getUrl(), domain.getUser());
	}
}
