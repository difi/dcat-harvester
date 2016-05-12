package no.difi.dcat.harvester.crawler;

import com.google.common.cache.LoadingCache;
import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;
import no.difi.dcat.harvester.crawler.converters.BrregAgentConverter;
import no.difi.dcat.harvester.validation.DcatValidation;
import no.difi.dcat.harvester.validation.ValidationError;
import org.apache.jena.atlas.io.StringWriterI;
import org.apache.jena.atlas.lib.Sync;
import org.apache.jena.atlas.lib.SystemUtils;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.*;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CrawlerJob implements Runnable {

	private List<CrawlerResultHandler> handlers;
	private DcatSource dcatSource;
	private AdminDataStore adminDataStore;
	private LoadingCache<URL, String> brregCache;

	private final Logger logger = LoggerFactory.getLogger(CrawlerJob.class);

	protected CrawlerJob(DcatSource dcatSource, AdminDataStore adminDataStore, LoadingCache<URL, String> brregCaache, CrawlerResultHandler... handlers) {
		this.handlers = Arrays.asList(handlers);
		this.dcatSource = dcatSource;
		this.adminDataStore = adminDataStore;
		this.brregCache = brregCaache;
	}

	public String getDcatSourceId() {
		return dcatSource.getId();
	}

	@Override
	public void run() {
		logger.info("[crawler_operations] [success] Started crawler job: {}", dcatSource.toString());
		LocalDateTime start = LocalDateTime.now();


		try {
			try{

				Dataset dataset = RDFDataMgr.loadDataset(dcatSource.getUrl());

				Model union = ModelFactory.createUnion(ModelFactory.createDefaultModel(), dataset.getDefaultModel());
				Iterator<String> stringIterator = dataset.listNames();

				while(stringIterator.hasNext()){
					union = ModelFactory.createUnion(union, dataset.getNamedModel(stringIterator.next()));
				}

				verifyModelByParsing(union);


				if(isEntryscape(union)){
					enrichForEntryscape(union);
				}

				if(isVegvesenet(union)){
					enrichForVegvesenet(union);
				}

				BrregAgentConverter brregAgentConverter = new BrregAgentConverter(brregCache);
				brregAgentConverter.collectFromModel(union);

				if (isValid(union)) {
					for (CrawlerResultHandler handler : handlers) {
						handler.process(dcatSource,union);
					}
				}

			}catch (JenaException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.syntaxError, e.getMessage());
				throw e;
			}catch (HttpException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.networkError, e.getMessage());
				throw e;
			}




			LocalDateTime stop = LocalDateTime.now();
			logger.info("[crawler_operations] [success] Finished crawler job: {}", dcatSource.toString() + ", Duration=" + returnCrawlDuration(start, stop));
		} catch (Exception e) {
			logger.error(String.format("[crawler_operations] [fail] Error running crawler job: %1$s, error=%2$s", dcatSource.toString(), e.toString()));
			e.printStackTrace();
			adminDataStore.addCrawlResults(dcatSource, DifiMeta.error, e.getMessage());

		}

	}

	protected void verifyModelByParsing(Model union) {
		StringWriter str = new StringWriter();
		union.write(str, RDFLanguages.strLangTurtle);
		RDFDataMgr.parse(new StreamRDF() {
			@Override
			public void start() {

			}

			@Override
			public void triple(Triple triple) {

			}

			@Override
			public void quad(Quad quad) {

			}

			@Override
			public void base(String base) {

			}

			@Override
			public void prefix(String prefix, String iri) {

			}

			@Override
			public void finish() {

			}
		}, new ByteArrayInputStream(str.toString().getBytes()), Lang.TTL);

		str = new StringWriter();
		union.write(str, RDFLanguages.strLangRDFXML);
		RDFDataMgr.parse(new StreamRDF() {
			@Override
			public void start() {

			}

			@Override
			public void triple(Triple triple) {

			}

			@Override
			public void quad(Quad quad) {

			}

			@Override
			public void base(String base) {

			}

			@Override
			public void prefix(String prefix, String iri) {

			}

			@Override
			public void finish() {

			}
		}, new ByteArrayInputStream(str.toString().getBytes()), Lang.RDFXML);

	}


	private boolean isEntryscape(Model union) {
		//detect entryscape data by doing a string match against the uri of a catalog
		ResIterator resIterator = union.listResourcesWithProperty(RDF.type, union.createResource("http://www.w3.org/ns/dcat#Catalog"));
		return resIterator.hasNext() && resIterator.nextResource().getURI().contains("://difi.entryscape.net/");
	}

	private boolean isVegvesenet(Model union) {
		//detect entryscape data by doing a string match against the uri of a catalog
		ResIterator resIterator = union.listResourcesWithProperty(RDF.type, union.createResource("http://www.w3.org/ns/dcat#Catalog"));
		return resIterator.hasNext() && resIterator.nextResource().getURI().contains("utv.vegvesen.no");
	}



	private void enrichForEntryscape(Model union) {

		// Add type DCTerms.RightsStatement to alle DCTerms.rights
		NodeIterator dctRights = union.listObjectsOfProperty(DCTerms.rights);
		while(dctRights.hasNext()){
			dctRights.next().asResource().addProperty(RDF.type, DCTerms.RightsStatement);
		}

		// Add type  DCTerms.Location to all DCTerms.spatial
		NodeIterator dctSpatial = union.listObjectsOfProperty(DCTerms.spatial);
		while(dctSpatial.hasNext()){
			dctSpatial.next().asResource().addProperty(RDF.type, DCTerms.Location);
		}

		// Replace all DCTerms.issued where the literal is not a date or datetime
		List<Statement> dctIssuedToDelete = new ArrayList<>();
		StmtIterator dctIssued = union.listStatements(new SimpleSelector(null, DCTerms.issued, (RDFNode) null));
		while(dctIssued.hasNext()){
			Statement statement = dctIssued.next();
			Literal literal = statement.getObject().asLiteral();
			if(literal.getDatatype().equals(XSDDatatype.XSDstring)){


				String string = literal.getString();
				dctIssuedToDelete.add(statement);

				if(string.contains(":")){
					//datetime
					Literal typedLiteral = ResourceFactory.createTypedLiteral(string, XSDDatatype.XSDdateTime);
					statement.getSubject().addLiteral(DCTerms.issued, typedLiteral);
				}else{
					//date

					Literal typedLiteral = ResourceFactory.createTypedLiteral(string, XSDDatatype.XSDdate);
					statement.getSubject().addLiteral(DCTerms.issued, typedLiteral);

				}


			}
		}

		dctIssuedToDelete.forEach(union::remove);


		// Remove DCTerms.accrualPeriodicity that are not according to DCAT AP 1.1
		List<Statement> accrualPeriodicityToDelete = new ArrayList<>();
		StmtIterator accrualPeriodicity = union.listStatements(new SimpleSelector(null, DCTerms.accrualPeriodicity, (RDFNode) null));
		while(accrualPeriodicity.hasNext()){
			Statement statement = accrualPeriodicity.next();
			String uri = statement.getObject().asResource().getURI();
			if(!uri.startsWith("http://publications.europa.eu/resource/authority/frequency/")){
				accrualPeriodicityToDelete.add(statement);
			}
		}

		accrualPeriodicityToDelete.forEach(union::remove);

	}

	private void enrichForVegvesenet(Model union) {

		// Make all use of dcat:contactPoint point to resources of type vcard:Kind
		NodeIterator contactPoint = union.listObjectsOfProperty(DCAT.contactPoint);
		while(contactPoint.hasNext()){
			Resource resource = contactPoint.next().asResource();
			resource.addProperty(RDF.type, union.createResource("http://www.w3.org/2006/vcard/ns#Kind"));
		}

		// Find a resource with foaf:name "Statens vegvesen" and use it as the dct:publisher for all dcat:Catalog(s)
		ResIterator catalogPublisher = union.listSubjectsWithProperty(RDF.type, DCAT.Catalog);
		while(catalogPublisher.hasNext()){
			Resource resource = catalogPublisher.next().asResource();
			ResIterator resIterator = union.listSubjectsWithProperty(FOAF.name, "Statens vegvesen");
			resource.addProperty(DCTerms.publisher, resIterator.nextResource());
		}

		// Change dcat:accessUrl from string literal to uri resource
		List<Statement> toDelete = new ArrayList<>();
		StmtIterator accessURL = union.listStatements(null, DCAT.accessUrl, (String) null);
		while(accessURL.hasNext()){
			toDelete.add(accessURL.nextStatement());
		}

		for (Statement statement : toDelete) {
			Resource subject = statement.getSubject();
			subject.addProperty(DCAT.accessUrl, union.createResource(statement.getObject().toString()));
			union.remove(statement);
		}


		// Make all uses of dct:publisher point to resources of type foaf:Agent
		NodeIterator dctPublisher = union.listObjectsOfProperty(DCTerms.publisher);
		while(dctPublisher.hasNext()){
			Resource resource = dctPublisher.next().asResource();
			resource.addProperty(RDF.type, FOAF.Agent);
		}


	}

	private boolean isValid(Model model) {

		final ValidationError.RuleSeverity[] status = {ValidationError.RuleSeverity.ok};
		final String[] message = {null};

		boolean validated = DcatValidation.validate(model, (error) -> {
			if (error.isError()) {
				status[0] = error.getRuleSeverity();
				message[0] = error.toString();
			}
			if (error.isWarning()) {
				if (status[0] != ValidationError.RuleSeverity.error) {
					status[0] = error.getRuleSeverity();
				}
			} else {
				status[0] = error.getRuleSeverity();
			}
            logger.error("[validation_" + error.getRuleSeverity() + "] " + error.toString() + ", " + this.dcatSource.toString());
		});

		Resource rdfStatus = null;

		switch (status[0]) {
			case error:
				rdfStatus = DifiMeta.error;
				break;
			case warning:
				rdfStatus = DifiMeta.warning;
				break;
			default:
				rdfStatus = DifiMeta.ok;
				break;
		}

		adminDataStore.addCrawlResults(dcatSource, rdfStatus, message[0]);

		return validated;
	}

	private String returnCrawlDuration(LocalDateTime start, LocalDateTime stop) {
		return String.valueOf(stop.compareTo(start));
	}

}
