package ongoing.backend.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface HttpSecurityResolver {
    void configure(HttpSecurity http) throws Exception;
}
