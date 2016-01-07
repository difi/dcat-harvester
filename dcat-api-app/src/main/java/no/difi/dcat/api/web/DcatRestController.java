package no.difi.dcat.api.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	/**
	 * 
	 * Supported urls:
	 * - /api/dcat
	 * - /api/dcat?format=jsonld
	 * - /api/dcat?format=rdf/xml
	 * 
	 * @param format
	 * @return
	 */
	@RequestMapping(value = "/api/dcat")
	public ResponseEntity<String> getDcat(@Valid @RequestParam(value="format", required=false) String format) {
		
		SupportedFormat supportedFormat = SupportedFormat.parseFormat(format);
		
		Model model = dcatDataStore.getAllDataCatalogues();
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			model.write(baos, supportedFormat.getLang());
			
			final HttpHeaders httpHeaders= new HttpHeaders();
		    httpHeaders.setContentType(supportedFormat.getMimetype());
			return new ResponseEntity<String>(baos.toString(), httpHeaders, HttpStatus.OK);
		} catch (IOException e) {
			logger.error("Error closing stream", e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static class FormatHelper {
		
	}
}
