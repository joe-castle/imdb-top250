package co.uk.joecastle.imdbtop250.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/v1/movies/**").permitAll()
                .antMatchers("/api/v1/user").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/movie/**").authenticated()
                .and()
                .oauth2Login(config -> config.defaultSuccessUrl("/", true))
                .logout().logoutSuccessUrl("/")
                .and()
                .csrf().disable();
    }

}
