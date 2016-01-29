package no.difi.dcat.harvester.crawler;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.LoadingCache;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.harvester.crawler.converters.BrregAgentConverter;
import no.difi.dcat.harvester.validation.DcatValidation;
import no.difi.dcat.harvester.validation.ValidationError;

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
			Model union = null;
			try{

				Dataset dataset = RDFDataMgr.loadDataset(dcatSource.getUrl());

				 union = ModelFactory.createUnion(ModelFactory.createDefaultModel(), dataset.getDefaultModel());
				Iterator<String> stringIterator = dataset.listNames();

				while(stringIterator.hasNext()){
					union = ModelFactory.createUnion(union, dataset.getNamedModel(stringIterator.next()));
				}

			}catch (RiotException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.syntaxError, e.getMessage());
				throw e;
			}catch (HttpException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.networkError, e.getMessage());
				throw e;
			}

			if(isEntryscape(union)){
				enrichForEntryscape(union);
			}
			
			BrregAgentConverter brregAgentConverter = new BrregAgentConverter(brregCache);
			brregAgentConverter.collectFromModel(union);

			if (isValid(union)) {
				for (CrawlerResultHandler handler : handlers) {
					handler.process(dcatSource,union);
				}
			}
			
			LocalDateTime stop = LocalDateTime.now();
			logger.info("[crawler_operations] [success] Finished crawler job: {}", dcatSource.toString() + ", Duration=" + returnCrawlDuration(start, stop));
		} catch (Exception e) {
			logger.error(String.format("[crawler_operations] [fail] Error running crawler job: %1$s, error=%2$s", dcatSource.toString(), e.toString()));
		}
		
	}

	private boolean isEntryscape(Model union) {
		//detect entryscape data by doing a string match against the uri of a catalog
		ResIterator resIterator = union.listResourcesWithProperty(RDF.type, union.createResource("http://www.w3.org/ns/dcat#Catalog"));
		return resIterator.hasNext() && resIterator.nextResource().getURI().contains("://difi.entryscape.net/");
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

				System.out.println(statement);

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

	private boolean isValid(Model model) {
		
		final ValidationError.RuleSeverity[] status = {ValidationError.RuleSeverity.ok};
		final String[] message = {null};

		boolean validated = DcatValidation.validate(model, (error) -> {
			if (error.isError()) {
				status[0] = error.getRuleSeverity();
				message[0] = error.toString();

				logger.error("[validation_" + status[0] + "] " + message[0] + " " + this.dcatSource.toString());
			}
			if (error.isWarning()) {
				if (status[0] != ValidationError.RuleSeverity.error) {
					status[0] = error.getRuleSeverity();
				}
				logger.warn("[validation_" + status[0] + "] " + message[0] + " " + this.dcatSource.toString());
			} else {
				status[0] = error.getRuleSeverity();
				logger.info("[validation_" + status[0] + "] "  + message[0] + " " + this.dcatSource.toString());
			}
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
