package ntt.common.jdbc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class HikariConfiguration {
    public static final int DEFAULT_MINIMUM_IDLE_CONNECTIONS = 1;

    @Value("${spring.datasource.hikari.connection-timeout:#{null}}")
    private Integer connectionTimeout;

    private Map<String, String> properties = new HashMap<>();
    public Map<String, String> getProperties() {
        properties.putIfAbsent("minimumIdle", String.valueOf(DEFAULT_MINIMUM_IDLE_CONNECTIONS));
        //Put your properties here
        if(connectionTimeout != null){
            properties.putIfAbsent("connectionTimeout", String.valueOf(connectionTimeout));
        }
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
