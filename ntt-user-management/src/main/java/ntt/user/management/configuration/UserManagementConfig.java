package ntt.user.management.configuration;
import lombok.RequiredArgsConstructor;
import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.common.jdbc.repository.JdbcRepository;
import ntt.common.jpa.repository.EntityRepository;
import ntt.common.jpa.repository.IEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class UserManagementConfig {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Bean
    IJdbcRepository jdbcRepository(JdbcTemplate jdbcTemplate){
        return new JdbcRepository(jdbcTemplate);
    }

    @Bean
    IEntityRepository entityRepository(){
        return new EntityRepository();
    }

}