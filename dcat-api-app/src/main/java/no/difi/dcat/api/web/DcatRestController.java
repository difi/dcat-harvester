package no.difi.dcat.api.web;

import com.google.common.cache.LoadingCache;
import no.difi.dcat.api.settings.FusekiSettings;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@CrossOrigin(origins = "*")
public class DcatRestController {

	@Autowired
	private FusekiSettings fusekiSettings;

	@Autowired
	private LoadingCache<Key, String> dcatCache;

	private final Logger logger = LoggerFactory.getLogger(DcatRestController.class);
	private DcatDataStore dcatDataStore;


	@PostConstruct
	public void initialize() {
		dcatDataStore = new DcatDataStore(new Fuseki(fusekiSettings.getDcatServiceUri()));
	}

	/**
	 * Supported urls:
	 * - /api/dcat
	 * - /api/dcat?format=jsonld
	 * - /api/dcat?format=rdf/xml
	 *
	 * @param format
	 * @return
	 */
	@RequestMapping(value = "/api/dcat")
	public ResponseEntity getDcat(@Valid @RequestParam(value = "format", required = false) String format) {

		SupportedFormat supportedFormat = SupportedFormat.parseFormat(format);

		try {

			Key key = new Key(fusekiSettings.getDcatServiceUri() + "/get?graph=urn:x-arq:UnionGraph", supportedFormat.getMimetype().toString());


			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpGet httpGet = new HttpGet(key.getUrl());

			httpGet.setHeader("Accept", key.getContentType());

			CloseableHttpResponse response1 = httpclient.execute(httpGet);


			HttpEntity entity = response1.getEntity();

			InputStream content = entity.getContent();

			HttpHeaders httpHeaders = new HttpHeaders();

			InputStreamResource inputStreamResource = new InputStreamResource(content);
			httpHeaders.setContentLength(entity.getContentLength());
			return new ResponseEntity(inputStreamResource, httpHeaders, HttpStatus.OK);



		} catch (Exception e) {
			logger.error("Error getting DCAT from Fuseki: " + e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@RequestMapping(value = "/api/admin")
	public ResponseEntity getAdmin(@Valid @RequestParam(value = "format", required = false) String format) {

		SupportedFormat supportedFormat = SupportedFormat.parseFormat(format);

		try {

			Key key = new Key(fusekiSettings.getAdminServiceUri() + "/get?graph=urn:x-arq:UnionGraph", supportedFormat.getMimetype().toString());

			return new ResponseEntity<String>(dcatCache.get(key), HttpStatus.OK);


		} catch (ExecutionException e) {
			logger.error("Error getting DCAT from Fuseki: " + e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	@RequestMapping(value = "/api/invalidateCache", method = RequestMethod.POST)
	public ResponseEntity invalidateCache() {
		dcatCache.invalidateAll();
		return new ResponseEntity<String>(HttpStatus.OK);

	}


	@RequestMapping(value = "/api/refreshCache", method = RequestMethod.POST)
	public ResponseEntity refreshCache() {

		List<Key> keyset = new ArrayList<>(dcatCache.asMap().keySet());
		keyset.forEach(dcatCache::refresh);

		return new ResponseEntity<String>(HttpStatus.OK);

	}


// This compression filter is quite slow, it's better to set up compression in tomcat.
//
//	@Bean
//	public CompressingFilter compressingFilter() {
//		CompressingFilter compressingFilter = new CompressingFilter();
//		return compressingFilter;
	/*
		<dependency>
			<groupId>net.sourceforge.pjl-comp-filter</groupId>
			<artifactId>pjl-comp-filter</artifactId>
			<version>1.7</version>
		</dependency>
	 */
//	}




}

