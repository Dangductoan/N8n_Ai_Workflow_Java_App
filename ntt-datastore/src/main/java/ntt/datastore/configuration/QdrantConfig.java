package ntt.datastore.configuration;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;
import ntt.datastore.properties.Settings;
import ntt.datastore.service.QdrantService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class QdrantConfig {

    private final Settings settings;
    public QdrantConfig( Settings settings) {
        this.settings = settings;
    }

    @Bean
    public QdrantClient QdrantGrpcClient() {
        //Official Java QdrantClient client use gRPC by default (port 6334).
        String qdrandHost = this.settings.getQdrantHost();
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(qdrandHost, 6334, false).build()
        );
        log.info("✅ Qdrant grpc client initialized successfully.");
        return client;
    }

//    @Bean
//    public QdrantService QdrantService() {
//        String qdrandHost = this.settings.getQdrantHost();
//        Integer qdrantPort = this.settings.getQdrantPort();
//        QdrantService client = new QdrantService(qdrandHost, qdrantPort);
//        log.info("✅ Qdrant http client initialized successfully.");
//        return client;
//    }

}
