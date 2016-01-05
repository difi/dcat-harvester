package no.difi.dcat.datastore;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.atlas.lib.FileOps;
import org.apache.jena.fuseki.jetty.JettyFuseki;
import org.apache.jena.fuseki.jetty.JettyServerConfig;
import org.apache.jena.fuseki.server.DataAccessPointRegistry;
import org.apache.jena.fuseki.server.FusekiEnv;
import org.apache.jena.fuseki.server.FusekiServer;
import org.apache.jena.fuseki.server.FusekiServerListener;
import org.apache.jena.fuseki.server.ServerInitialConfig;
import org.apache.jena.fuseki.server.SystemState;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.StoreConnection;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

/**
 * Created by havardottestad on 05/01/16.
 */
public class FusekiTest {

      JettyFuseki server;

      @org.junit.Before
      public void setUp() throws Exception {

            SystemState.location = Location.mem() ;
            SystemState.init$() ;

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