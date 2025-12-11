package ntt.datastore.configuration;

import lombok.extern.slf4j.Slf4j;
import ntt.datastore.properties.N8nProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class N8nConfig {

    private final N8nProperties properties;

    public N8nConfig(N8nProperties properties) {
        this.properties = properties;
    }


    //Create your Bean here
}
