package no.difi.dcat.api.synd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FeedController {
	
	@Autowired
	private DcatFeed dcatFeed;
	
	@RequestMapping(value="/api/atom/feed", method=RequestMethod.GET)
	public ModelAndView getAtomContent() {
		ModelAndView mav = new ModelAndView(new DcatAtomView());
		mav.addObject("feeds", dcatFeed.createFeed());
		return mav;
	}
	
	@RequestMapping(value="/api/rss/feed", method=RequestMethod.GET)
	public ModelAndView getRssContent() {
		ModelAndView mav = new ModelAndView(new DcatRssView());
		mav.addObject("feeds", dcatFeed.createFeed());
		return mav;
	}
}