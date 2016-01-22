package no.difi.dcat.harvester.crawler.converters;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.acando.semtech.xmltordf.Builder;
import no.acando.semtech.xmltordf.PostProcessing;
import no.acando.semtech.xmltordf.XmlToRdfObject;

public class BrregAgentConverter {

	private XmlToRdfObject xmlToRdfObject;

	private static Logger logger = LoggerFactory.getLogger(BrregAgentConverter.class);

	public BrregAgentConverter() {
		xmlToRdfObject = Builder.getObjectBasedBuilder()
				.setBaseNamespace("http://data.brreg.no/meta/", Builder.AppliesTo.bothElementsAndAttributes)
				.autoConvertShallowChildrenToProperties(true)
				.autoConvertShallowChildrenWithAutoDetectLiteralProperties(true)
				.addTransformForClass("http://data.brreg.no/meta/navn", FOAF.name.getURI())
				.addTransformForClass("http://data.brreg.no/meta/enhet", FOAF.Agent.getURI()).build();
	}

	public Model convert(InputStream inputStream) {
		try {
			return convert(xmlToRdfObject.convertForPostProcessing(inputStream));
		} catch (Exception e) {
			logger.error("Error converting InputStream", e);
			return ModelFactory.createDefaultModel();
		}
	}

	private Model convert(PostProcessing postProcessing) {
		Model extractedModel = ModelFactory.createDefaultModel();
		try {
			extractedModel = postProcessing.outputIntermediaryModels(new File("brreg/intermediate"))
					.sparqlTransform(new File("brreg/transforms"))
					.extractConstruct(new File("brreg/construct")).getExtractedModel();

			applyNamespaces(extractedModel);

			return extractedModel;
		} catch (Exception e) {
			logger.error("Error converting PostProcessing", e);
		}

		return extractedModel;
	}

	private static void applyNamespaces(Model extractedModel) {
		// extractedModel.setNsPrefix("meta", "http://data.brreg.no/meta/");
		extractedModel.setNsPrefix("foaf", FOAF.getURI());
	}

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://data.brreg.no/enhetsregisteret/underenhet/814716902.xml");

		InputStream inputStream = url.openConnection().getInputStream();

		BrregAgentConverter converter = new BrregAgentConverter();
		Model model = converter.convert(inputStream);

		model.getWriter("TTL").write(model, System.out, null);
	}
}
