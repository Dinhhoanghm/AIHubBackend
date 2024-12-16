package ongoing.backend.config.security.config;


import ongoing.backend.config.security.HttpSecurityResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final HttpSecurityResolver httpSecurityResolver;

    public SecurityConfig(
                          HttpSecurityResolver httpSecurityResolver) {
        this.httpSecurityResolver = httpSecurityResolver;

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        httpSecurityResolver.configure(http);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
