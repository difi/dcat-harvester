package no.difi.dcat.harvester.crawler;

import no.difi.dcat.harvester.validation.DcatValidation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by havardottestad on 04/01/16.
 */
public class CrawlerResultHandlerTest {

      @org.junit.Before
      public void setUp() throws Exception {

      }

      @org.junit.After
      public void tearDown() throws Exception {

      }

      @org.junit.Test
      public void testValidation() throws Exception {

            CrawlerResultHandler crawlerResultHandler = new CrawlerResultHandler("");

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("validation-test-data/").getFile());
            Arrays.stream(file.listFiles((f)->f.getName().endsWith(".rdf"))).forEach((f)->{
                  Model model = null;
                  try {
                        model = FileManager.get().loadModel(f.getCanonicalPath());
                  } catch (IOException e) {
                        e.printStackTrace();
                  }

                  boolean validate = DcatValidation.validate(model, (error)-> System.out.println(error));
            });






      }
}