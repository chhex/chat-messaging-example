package chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Enable authentication with three in-memory users: UserA, UserB and UserC.
	 *
	 * Spring Security will provide a default login form where insert username and
	 * password.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
				// Defines three users with their passwords and roles
				.inMemoryAuthentication()
				.withUser("u01").password("u01").roles("USER")
				.and().withUser("u11").password("u11").roles("USER")
				.and().withUser("u21").password("u21").roles("USER");
		return;
	}

	/**
	 * Disable CSRF protection (to simplify this demo) and enable the default login
	 * form.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf()
				// ignore our stomp endpoints since they are protected using Stomp headers
	            .ignoringAntMatchers("/chat/**")
				.and()
			      .headers()
			        .frameOptions().sameOrigin()
			      .and()
				.authorizeRequests()
//	            .antMatchers("/chat/**")
//	            .permitAll()
//	            .and()
//	            .authorizeRequests()
				.anyRequest()
				.authenticated()
				.and().formLogin().
				and().httpBasic();
		return;
	}

} // class WebSecurityConfig
