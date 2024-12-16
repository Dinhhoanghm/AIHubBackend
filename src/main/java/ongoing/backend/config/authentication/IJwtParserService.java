package ongoing.backend.config.authentication;


import ongoing.backend.config.model.SimpleSecurityUser;

public interface IJwtParserService {
    SimpleSecurityUser parserUser(String token);
}
