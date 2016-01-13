package no.difi.dcat.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import no.difi.dcat.api.synd.DcatAtomView;
import no.difi.dcat.api.synd.DcatRssView;

@Configuration 
@ComponentScan("no.difi.dcat.api.atom") 
@EnableWebMvc
public class AtomConfiguration extends WebMvcConfigurerAdapter  {  
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new DcatAtomView(), new DcatRssView());
    }
}