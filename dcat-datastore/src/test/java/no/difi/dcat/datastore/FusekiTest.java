package no.difi.dcat.datastore;

import org.apache.commons.io.FileUtils;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.atlas.lib.FileOps;
import org.apache.jena.fuseki.jetty.JettyFuseki;
import org.apache.jena.fuseki.jetty.JettyServerConfig;
import org.apache.jena.fuseki.server.*;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.StoreConnection;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by havardottestad on 05/01/16.
 */
public class FusekiTest {

	JettyFuseki server;

	@org.junit.Before
	public void setUp() throws Exception {

		SystemState.location = Location.mem();
		SystemState.init$();

		File dcatDB = new File("src/test/resources/fuseki-home/db/dcat");
		File adminDB = new File("src/test/resources/fuseki-home/db/admin");

		dcatDB.mkdirs();
		adminDB.mkdirs();

		FileUtils.cleanDirectory(dcatDB);
		FileUtils.cleanDirectory(adminDB);

		fuseki();

	}

	@org.junit.After
	public void tearDown() throws Exception {

		if (server != null) {
			server.stop();
		}


		StoreConnection.reset();


		Thread.sleep(1000);
		server = null;
		// Clear out the registry.
		Collection<String> keys = Iter.toList(DataAccessPointRegistry.get().keys().iterator());
		for (String k : keys) {
			DataAccessPointRegistry.get().remove(k);
		}
		// Clear configuration directory.
		System.out.println(FusekiServer.dirConfiguration.toFile());
		FileOps.clearAll(FusekiServer.dirConfiguration.toFile());

		Thread.sleep(1000);
	}


	@Test
	public void testThatEmbeddedFusekiWorks() {

		Fuseki fuseki = new Fuseki("http://localhost:3131/dcat/");
		ResultSet select = fuseki.select("select * where {?a ?b ?c}");
		String s = ResultSetFormatter.asText(select);
		System.out.println(s);

		Model defaultModel = ModelFactory.createDefaultModel();
		defaultModel.createResource().addLiteral(RDFS.label, "yay");
		fuseki.update("http://example.com/a", defaultModel);
		System.out.println(ResultSetFormatter.asText(fuseki.select("select * where {?a ?b ?c}")));

		Fuseki fuseki2 = new Fuseki("http://localhost:3131/admin/");
		fuseki2.select("select * where {?a ?b ?c}");

	}


	@Test
	public void testThatEmbeddedFusekiWorks2() {

		Fuseki fuseki = new Fuseki("http://localhost:3131/dcat/");
		System.out.println(ResultSetFormatter.asText(fuseki.select("select * where {?a ?b ?c}")));

		Model defaultModel = ModelFactory.createDefaultModel();
		defaultModel.createResource().addLiteral(RDFS.label, "yay2");
		fuseki.update("http://example.com/a", defaultModel);

		System.out.println(ResultSetFormatter.asText(fuseki.select("select * where {?a ?b ?c}")));

		Fuseki fuseki2 = new Fuseki("http://localhost:3131/admin/");
		fuseki2.select("select * where {?a ?b ?c}");

	}


	@Test
	public void testFusekiEndpointSlash() {
		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");
		Fuseki fuseki2 = new Fuseki("http://localhost:3131/admin");

		fuseki.drop("");
		fuseki2.drop("");


	}


	@Test
	public void testAddUser() throws UserAlreadyExistsException {
		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");

		Map<String, String> testUserName = adminDataStore.getUser("testUserName");

		assertEquals("The username should be testUserName.", "testUserName", testUserName.get("username"));

		ResultSet select = fuseki.select("select * where {?a foaf:accountName \"testUserName\"}");
		int count = 0;
		while (select.hasNext()) {
			count++;
			select.next();
		}


		assertEquals("Should be exactly 1 user with this username", 1, count);
	}

	@Test(expected = UserAlreadyExistsException.class)
	public void testAddMultipleUsers() throws UserAlreadyExistsException {
		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");
		adminDataStore.addUser("testUserName", "", "");

	}

	@Test
	public void testAddDcatSource() throws UserAlreadyExistsException, Exception {
		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");

		DcatSource dcatSource = new DcatSource();
		dcatSource.setDescription("desc");
		dcatSource.setUser("testUserName");
		dcatSource.setUrl("http://url");

		dcatSource = adminDataStore.addDcatSource(dcatSource);
		assertNotNull("There should exist a dcat source", dcatSource);

		Optional<DcatSource> dcatSourceById = adminDataStore.getDcatSourceById(dcatSource.getId());

		assertTrue("The dcat source should exist in the database", dcatSourceById.isPresent());
		DcatSource fromFuseki = dcatSourceById.get();
		assertEquals("Url should be equal", dcatSource.getUrl(), fromFuseki.getUrl());
		assertEquals("User should be equal", dcatSource.getUser(), fromFuseki.getUser());
		assertEquals("Description should be equal", dcatSource.getDescription(), fromFuseki.getDescription());
		assertEquals("Graph should be equal", dcatSource.getGraph(), fromFuseki.getGraph());
		assertEquals("Id should be equal", dcatSource.getId(), fromFuseki.getId());

	}

	@Test
	public void testGetAllDcatSourcesForUser() throws UserAlreadyExistsException, Exception {

		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");

		adminDataStore.addDcatSource(new DcatSource(null, "sourc1", "http:1", "testUserName"));
		adminDataStore.addDcatSource(new DcatSource(null, "sourc2", "http:2", "testUserName"));
		adminDataStore.addDcatSource(new DcatSource(null, "sourc3", "http:3", "testUserName"));


		adminDataStore.addUser("testUserName2", "", "");

		adminDataStore.addDcatSource(new DcatSource(null, "sourc21", "http:21", "testUserName2"));
		adminDataStore.addDcatSource(new DcatSource(null, "sourc22", "http:22", "testUserName2"));

		List<DcatSource> testUserNameDcatSources = adminDataStore.getDcatSourcesForUser("testUserName");
		List<DcatSource> testUserName2DcatSources = adminDataStore.getDcatSourcesForUser("testUserName2");

		assertEquals("", 3, testUserNameDcatSources.size());
		assertEquals("", 2, testUserName2DcatSources.size());

	}


	@Test
	public void testWhenNoSourcesForUse() throws UserAlreadyExistsException, Exception {

		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");

		List<DcatSource> testUserNameDcatSources = adminDataStore.getDcatSourcesForUser("testUserName");
		List<DcatSource> testUserName2DcatSources = adminDataStore.getDcatSourcesForUser("testUserName2");

		assertEquals("", 0, testUserNameDcatSources.size());
		assertEquals("", 0, testUserName2DcatSources.size());

	}

	@Test
	public void testUpdateDataSource() throws UserAlreadyExistsException, Exception {
		Fuseki fuseki = new Fuseki("http://localhost:3131/admin/");

		AdminDataStore adminDataStore = new AdminDataStore(fuseki);
		adminDataStore.addUser("testUserName", "", "");

		DcatSource dcatSource = new DcatSource();
		dcatSource.setDescription("desc");
		dcatSource.setUser("testUserName");
		dcatSource.setUrl("http://url");

		dcatSource = adminDataStore.addDcatSource(dcatSource);
		assertNotNull("There should exist a dcat source", dcatSource);

		Optional<DcatSource> dcatSourceById = adminDataStore.getDcatSourceById(dcatSource.getId());

		assertTrue("The dcat source should exist in the database", dcatSourceById.isPresent());
		DcatSource fromFuseki = dcatSourceById.get();

		fromFuseki.setDescription("hello");
		fromFuseki.setUrl("different url");
		fromFuseki.setGraph(null);

		adminDataStore.addDcatSource(fromFuseki);

		Optional<DcatSource> dcatSourceById2 = adminDataStore.getDcatSourceById(dcatSource.getId());
		assertTrue("The dcat source should exist in the database", dcatSourceById2.isPresent());
		DcatSource fromFuseki2 = dcatSourceById2.get();



		assertEquals("Url should be equal", fromFuseki.getUrl(), fromFuseki2.getUrl());
		assertEquals("User should be equal", fromFuseki.getUser(), fromFuseki2.getUser());
		assertEquals("Description should be equal", fromFuseki.getDescription(), fromFuseki2.getDescription());
		assertEquals("Graph should be equal", dcatSource.getGraph(), fromFuseki2.getGraph());
		assertEquals("Id should be equal", fromFuseki.getId(), fromFuseki2.getId());

	}


	public void fuseki() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("fuseki-embedded.ttl").getFile());
		File fusekihome = new File(classLoader.getResource("fuseki-home").getFile());

		FusekiEnv.FUSEKI_HOME = fusekihome.toPath();
		FusekiEnv.FUSEKI_BASE = FusekiEnv.FUSEKI_HOME;
		ServerInitialConfig serverSetup = new ServerInitialConfig();
		serverSetup.fusekiServerConfigFile = file.getCanonicalPath();
		FusekiServerListener.initialSetup = serverSetup;
		JettyFuseki.initializeServer(make(3131, false, true));
		JettyFuseki instance = JettyFuseki.instance;
		instance.start();
		server = instance;
	}


	public static JettyServerConfig make(int port, boolean allowUpdate, boolean listenLocal) {
		JettyServerConfig config = new JettyServerConfig();
		// Avoid any persistent record.
		config.port = port;
		config.contextPath = "/";

		config.loopback = listenLocal;
		config.jettyConfigFile = null;
		config.enableCompression = true;
		config.verboseLogging = false;
		return config;
	}


}