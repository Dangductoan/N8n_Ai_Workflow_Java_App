package ntt.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
//You need to tell the Feign scanner where to locate the Feign Interfaces
@EnableFeignClients(basePackages = {"ntt.common.api"})
//@PropertySources({
//        @PropertySource("classpath:application.properties"),
//})
public class TestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestingApplication.class, args);
    }


}
