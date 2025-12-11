/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-12
 * Description :
 * - Customize the default ObjectMapper to register the java time module for
 *   proper serialization and deserialization of java LocalDateTime
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.system.management.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonConfiguration.class);

    @Autowired
    public JacksonConfiguration(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        LOGGER.info("Registered JavaTimeModule with Jackson ObjectMapper");
    }
}
