import no.difi.dcat.harvester.validation.DcatValidation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.util.UUID;

/**
 * Created by havardottestad on 22/01/16.
 */
public class Main {

	static final String EXAMPLE = "http://example.com/";

	public static void main(String[] args) {

		Model defaultModel = ModelFactory.createDefaultModel();


		Resource resource = defaultModel.createResource(EXAMPLE + UUID.randomUUID().toString());
		


		DcatValidation.validate(defaultModel, error -> System.out.println(error));




	}

}
