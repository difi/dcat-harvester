import no.difi.dcat.harvester.validation.DcatValidation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import java.util.UUID;

/**
 * Created by havardottestad on 22/01/16.
 */
public class Main {

	static final String EXAMPLE = "http://example.com/";

	public static void main(String[] args) {

		Model defaultModel = ModelFactory.createDefaultModel();


		Resource resource = defaultModel.createResource(EXAMPLE + UUID.randomUUID().toString());

		resource.addProperty(RDF.type, DCAT.Catalog);


		DcatValidation.validate(defaultModel, error -> {if(error.isError())System.out.println(error);});




	}

	static class DCAT{
		static final String URI = "http://www.w3.org/ns/dcat#";
		static final Resource Catalog = ResourceFactory.createResource(URI+"Catalog");

	}

}
