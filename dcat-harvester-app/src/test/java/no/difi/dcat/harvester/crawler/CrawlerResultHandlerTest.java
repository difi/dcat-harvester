package no.difi.dcat.harvester.crawler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import no.difi.dcat.harvester.validation.DcatValidation;

/**
 * Created by havardottestad on 04/01/16.
 */
public class CrawlerResultHandlerTest {

      @Before
      public void setUp() throws Exception {

      }

      @After
      public void tearDown() throws Exception {

      }

      @Test
      public void testValidation() throws Exception {

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("validation-test-data/").getFile());
            Arrays.stream(file.listFiles((f)->f.getName().endsWith(".rdf"))).forEach((f)->{
                  Model model = null;
                  try {
                        model = FileManager.get().loadModel(f.getCanonicalPath());
                  } catch (IOException e) {
                        e.printStackTrace();
                  }

                  DcatValidation.validate(model, (error)-> System.out.println(error));
            });






      }
}