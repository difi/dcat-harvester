package no.difi.dcat.harvester.validation;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by havardottestad on 04/01/16.
 */
public class DcatValidation {

      public static boolean validate(Model model, ValidationHandler validationHandler) {
            if (validationHandler == null) {
                  validationHandler = (error) -> {
                  };
            }

            ClassLoader classLoader = DcatValidation.class.getClassLoader();
            File file = new File(classLoader.getResource("validation-rules").getFile());

            final boolean[] valid = {true};
            final ValidationHandler finalValidationHandler = validationHandler;

            Arrays.stream(file.listFiles((dir) -> dir.getName().endsWith(".rq")))
                        .forEach((f) -> {
                              try {
                                    String query = FileUtils.readFileToString(f);
                                    ResultSet resultSet = QueryExecutionFactory.create(query, model).execSelect();

                                    while (resultSet.hasNext()) {
                                          ValidationError error = new ValidationError(resultSet.next());
                                          finalValidationHandler.handle(error);
                                          if (error.isError()) {
                                                valid[0] = false;
                                          }
                                    }
                              } catch (IOException e) {
                                    e.printStackTrace();
                              }

                        });


            return valid[0];

      }

}
