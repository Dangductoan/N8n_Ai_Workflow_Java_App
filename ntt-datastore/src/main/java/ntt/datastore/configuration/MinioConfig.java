package ntt.datastore.configuration;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import ntt.datastore.properties.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    private final Settings settings;
    public MinioConfig(Settings settings) {
        this.settings = settings;
    }

    @Bean
    public MinioClient minioClient() {
        try {
            log.info("Creating MinIO client for endpoint: {}", settings.getMinioEndpoint());
            boolean secure = false;
            String minioEndpoint = "localhost";
            Integer minioPort = 9000;
            String[] endpoints = settings.getMinioEndpoint()
                    .replace("http://", "")
                    .replace("https://", "")
                    .split(":");
            if (endpoints.length == 2) {
                minioEndpoint = endpoints[0];
                minioPort = Integer.parseInt(endpoints[1]);
            } else {
                minioEndpoint = endpoints[0];
            }
            var minioClient = MinioClient.builder()
                    .endpoint(minioEndpoint, minioPort, secure)
                    .credentials(settings.getMinioAccessKey(), settings.getMinioSecretKey())
                    .build();
            log.info("✅ MinIO client initialized successfully.");
            return minioClient;
        }catch (Exception e){
            log.error("❌ MinIO client initialization failed");
            throw new RuntimeException("MinIO client initialization failed.");
        }

    }
}