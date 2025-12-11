/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-13
 * Description : Create Storage Service to work with multiple databases:
 * - MinIO
 * - N8n
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.datastore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class DataStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataStoreApplication.class, args);
    }



}
