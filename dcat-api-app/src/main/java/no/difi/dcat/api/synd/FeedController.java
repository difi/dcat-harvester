package no.difi.dcat.api.synd;

import javax.annotation.PostConstruct;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import no.difi.dcat.api.settings.FusekiSettings;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;

@Controller
public class FeedController {
	
	@Autowired
	private DcatFeed dcatFeed;
	
	@Autowired
	private FusekiSettings fusekiSettings;
	private DcatDataStore dcatDataStore;

	private final Logger logger = LoggerFactory.getLogger(FeedController.class);

	@PostConstruct
	public void initialize() {
		dcatDataStore = new DcatDataStore(new Fuseki(fusekiSettings.getDcatServiceUri()));
	}
	
	@RequestMapping(value="/api/rss/feed", method=RequestMethod.GET)
	public ModelAndView getRssContent() {
		ModelAndView mav = new ModelAndView(new DcatRssView());
		
		Model model = dcatDataStore.getAllDataCatalogues();
		
		mav.addObject("feeds", dcatFeed.createFeed(model));
		return mav;
	}
}