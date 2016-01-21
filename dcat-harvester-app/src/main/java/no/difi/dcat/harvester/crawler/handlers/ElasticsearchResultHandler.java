package no.difi.dcat.harvester.crawler.handlers;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class ElasticsearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(ElasticsearchResultHandler.class);

	String prefixes = String.join("\n",
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>",
			"PREFIX difiMeta: <http://dcat.difi.no/metadata/>",
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
			"PREFIX dct: <http://purl.org/dc/terms/>",
			"PREFIX dcat: <http://www.w3.org/ns/dcat#>"
	);

	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");

		Dataset dataset = DatasetFactory.create(model);


		String join = String.join("\n", prefixes,
				"delete {",
				"	?catalog dcat:dataset ?dataset",
				"}",
				"insert {",
				"	?dataset dcat:inCatalog ?catalog",
				"}",
				"where {",
				"	?catalog dcat:dataset ?dataset",
				"}"
		);
		UpdateRequest updates = UpdateFactory.create(join);
		UpdateExecutionFactory.create(updates, dataset).execute();


		// Now denoramlisert to dataset level
		try {
			generateJsonObject(dataset, "dcat:inCatalog");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonLdError jsonLdError) {
			jsonLdError.printStackTrace();
		}


		String join2 = String.join("\n", prefixes,
				"delete {",
				"	?catalog dcat:distribution ?dataset",
				"}",
				"insert {",
				"	?dataset dcat:inDataset ?catalog",
				"}",
				"where {",
				"	?catalog dcat:distribution ?dataset",
				"}"
		);
		UpdateRequest updates2 = UpdateFactory.create(join2);
		UpdateExecutionFactory.create(updates2, dataset).execute();


		String construct = String.join("\n", prefixes,

				"construct {",
				"	?a dct:description ?desc .",
				"	?a dcat:inDataset ?inDataset .",
				"	?a dct:issued ?issued .",
				"	?a dcat:inCatalog ?inCatalog.",
				"}",
				"where {",
				"	OPTIONAL{?a dct:description ?desc .}",
				"	OPTIONAL{?a dct:issued ?issued .}",

				"	OPTIONAL{?a dcat:inDataset ?inDataset}",
				"	OPTIONAL{?a dcat:inCatalog ?inCatalog}",

				"}"
		);

		Query query = QueryFactory.create(construct);
		Model model1 = QueryExecutionFactory.create(query, dataset).execConstruct();


		//Now denoramlised to distribution level
		try {
			JsonObject jsonObject = generateJsonObject(model1, "dcat:inDataset");
			jsonObject.remove("@context");
			JsonArray asJsonArray = jsonObject.get("@graph").getAsJsonArray();
			asJsonArray.forEach((j) -> {
				String s = new GsonBuilder().setPrettyPrinting().create().toJson(j);
				System.out.println(s);
			});

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonLdError jsonLdError) {
			jsonLdError.printStackTrace();
		}


		// TODO Auto-generated method stub

	}

	private JsonObject generateJsonObject(Model model, final String nestedPropertyName) throws IOException, JsonLdError {
		Dataset dataset = DatasetFactory.create(model);
		return generateJsonObject(dataset, nestedPropertyName);
	}



	private JsonObject generateJsonObject(Dataset dataset, final String nestedPropertyName) throws IOException, JsonLdError {
		Model defaultModel = dataset.getDefaultModel();
		defaultModel.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
		defaultModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		defaultModel.setNsPrefix("dct", "http://purl.org/dc/terms/");



		StringWriter stringWriter = new StringWriter();

		defaultModel.write(stringWriter, Lang.JSONLD.getLabel());


		Object o = JsonUtils.fromInputStream(new ByteArrayInputStream(stringWriter.toString().getBytes()));


		String jsonString = String.join("\n", "{",

				" '@context': {" ,
				"  	'foaf': 'http://xmlns.com/foaf/0.1/'," ,
				"  	'dct': 'http://purl.org/dc/terms/'," ,
				"  	'dcat': 'http://www.w3.org/ns/dcat#'," ,
				"	'issued' : {" ,
				"      	'@id' : 'http://purl.org/dc/terms/issued'," ,
				"      	'@type' : 'http://www.w3.org/2001/XMLSchema#date'" ,
				" 	}," ,
				"	'description' : {" ,
				"		'@container':'@set', " ,
				"      	'@id' : 'http://purl.org/dc/terms/description'" ,
				"	}," ,
				"	'modified' : {" ,
				"      	'@id' : 'http://purl.org/dc/terms/modified'," ,
				"      	'@type' : 'http://www.w3.org/2001/XMLSchema#date'" ,
				"    	}" ,
				"}" ,
				",",
				"'" + nestedPropertyName + "': {" ,
				"  '@embed': true " ,
				"}" ,


				"}");

		jsonString = jsonString.replaceAll("'", "\"");
		JsonLdOptions jsonLdOptions = new JsonLdOptions();

		Map<String, Object> frame = JsonLdProcessor.frame(o, JsonUtils.fromString(jsonString), jsonLdOptions);


		System.out.println("------------");

		System.out.println();

		System.out.println(JsonUtils.toPrettyString(frame));
		System.out.println("------------");

		JsonObject jsonObject = new Gson().fromJson(JsonUtils.toPrettyString(frame), JsonObject.class);

		return jsonObject;


	}

}
