package no.difi.dcat.harvester.crawler.converters;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.DCTerms;
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

	private Model convert(InputStream inputStream) {
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
					.sparqlTransform(new File("src/main/resources/brreg/transforms"))
					.extractConstruct(new File("src/main/resources/brreg/construct")).getExtractedModel();

			applyNamespaces(extractedModel);
			
			return extractedModel;
		} catch (Exception e) {
			logger.error("Error converting PostProcessing", e);
		}

		return extractedModel;
	}
	
	public void collectFromModel(Model model) {
		NodeIterator iterator = model.listObjectsOfProperty(DCTerms.publisher);
		
		while (iterator.hasNext()) {
			RDFNode next = iterator.next();
			if (next.isResource() && next.asResource().getURI().contains("data.brreg.no")) {
				String uri = next.asResource().getURI();
				collectFromUri(uri, model);
			} else {
				logger.trace("{} either is not a resource or does not contain \"data.brreg.no\"", next);
			}
		}
		
		
	}
	
	private void collectFromUri(String uri, Model model) {
		if (!uri.endsWith(".xml")) {
			uri = uri + ".xml";
		}
			
		try {
			URL url = new URL(uri);
			model.add(convert(url.openStream()));
		} catch (Exception e) {
			logger.warn("Failed to look up publisher: {}", uri, e);
		}	
	}

	private static void applyNamespaces(Model extractedModel) {
		extractedModel.setNsPrefix("foaf", FOAF.getURI());
	}

	public static void main(String[] args) throws Exception {
		Model model = ModelFactory.createDefaultModel();
		
		BrregAgentConverter converter = new BrregAgentConverter();
//		converter.collectFromUri("http://data.brreg.no/enhetsregisteret/underenhet/814716902", model);
//
//		model.getWriter("TTL").write(model, System.out, null);
		
		Model model2 = ModelFactory.createDefaultModel();
		
		model2.getReader("JSONLD").read(model2, "src/test/resources/brreg-link.jsonld");
		
		converter.collectFromModel(model2);
		
		model.getWriter("TTL").write(model2, System.out, null);
	}
}
