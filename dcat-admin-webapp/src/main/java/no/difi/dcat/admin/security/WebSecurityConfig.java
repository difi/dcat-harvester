package no.difi.dcat.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private FusekiUserDetailsService fusekiUserDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable() //TODO: enable CSRF protection: https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html#csrf-include-csrf-token
            .authorizeRequests()
            	.antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {    	
        auth
            .userDetailsService(fusekiUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }
	
}
