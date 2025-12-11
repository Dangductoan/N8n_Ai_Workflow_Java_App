package ntt.workflow.auth.configuration;

import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.common.jdbc.repository.JdbcRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MediaConfiguration {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    IJdbcRepository jdbcRepository(JdbcTemplate jdbcTemplate){
        return new JdbcRepository(jdbcTemplate);
    }

}
