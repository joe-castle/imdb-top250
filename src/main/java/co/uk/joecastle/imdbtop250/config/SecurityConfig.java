package co.uk.joecastle.imdbtop250.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/movies/**").authenticated()
                .and()
                .oauth2Login(config -> config.defaultSuccessUrl("/", true))
                .logout().logoutSuccessUrl("/")
                .and()
                .csrf().disable();
    }

}
