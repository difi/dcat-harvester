package no.difi.dcat.api.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.api.settings.FusekiSettings;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;

@RestController
@CrossOrigin(origins = "*")
public class DcatRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private DcatDataStore dcatDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(DcatRestController.class);

	@PostConstruct
	public void initialize() {
		dcatDataStore = new DcatDataStore(new Fuseki(fusekiSettings.getDcatServiceUri()));
	}
	
	@RequestMapping(value = "/api/dcat", produces = "application/json+ld")
	public ResponseEntity<String> getDcat() {
		Model model = dcatDataStore.getAllDataCatalogues();
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			model.write(baos, "JSONLD");
			return new ResponseEntity<String>(baos.toString(), HttpStatus.OK);
		} catch (IOException e) {
			logger.error("Error closing stream", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
