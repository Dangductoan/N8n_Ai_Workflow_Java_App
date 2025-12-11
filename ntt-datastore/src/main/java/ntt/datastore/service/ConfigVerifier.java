package ntt.datastore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntt.datastore.properties.Settings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigVerifier implements CommandLineRunner {

    private final Settings settings;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Verifying Consul Configuration ===");
        log.info("QDRANT_HOST: {}", settings.getQdrantHost());
        log.info("QDRANT_PORT: {}", settings.getQdrantPort());
        log.info("MYSQL_SERVER: {}", settings.getMysqlServer());
        log.info("MYSQL_PORT: {}", settings.getMysqlPort());
        log.info("REDIS_HOST: {}", settings.getRedisHost());
        log.info("REDIS_PORT: {}", settings.getRedisPort());
        log.info("MINIO_ENDPOINT: {}", settings.getMinioEndpoint());
        log.info("======================================");
    }
}
