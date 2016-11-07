package no.difi.dcat.admin.web.dcat;

import no.difi.dcat.admin.settings.ApplicationSettings;

/**
 * Created by havardottestad on 12/05/16.
 */
public class KibanaLink{

	String port;
    String firstHalf;
    String secondHalf;

    KibanaLink(ApplicationSettings applicationSettings){
    	
    	port = applicationSettings.getKibanaPort();
        firstHalf = applicationSettings.getKibanaLinkFirstHalf();
        secondHalf = applicationSettings.getKibanaLinkSecondHalf();
    }

    public String getPort(){
    	return port;
    }
    
    public String getFirstHalf() {
        return firstHalf;
    }

    public String getSecondHalf() {
        return secondHalf;
    }
}