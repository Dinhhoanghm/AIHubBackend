package ongoing.backend.config.http;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static ongoing.backend.config.jackson.JsonMapper.getObjectMapper;


@Configuration
@RequiredArgsConstructor
public class HTTPConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .messageConverters(new MappingJackson2HttpMessageConverter(getObjectMapper()))
                .build();
    }
}