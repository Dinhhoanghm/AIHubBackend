package ongoing.backend.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.stereotype.Service;

@Service
public class DefaultHttpSecurityResolver implements HttpSecurityResolver {
  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
      .exceptionHandling().and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeRequests()
      .antMatchers("/file/**").permitAll()
      .antMatchers("/api/**").permitAll()
      .anyRequest()
      .permitAll();
  }
}
