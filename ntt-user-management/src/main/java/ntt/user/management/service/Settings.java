 /**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-14
 * Description : Getting Consul Settings
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */

 package ntt.user.management.service;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope  // Allow refresh config runtime
@ConfigurationProperties()
public class Settings {

    // ===== MySQL Settings =====
    @Value("${MYSQL_SERVER}")
    private String mysqlServer;

    @Value("${MYSQL_PORT:3306}")
    private Integer mysqlPort;

    @Value("${MYSQL_USER:workflow_user}")
    private String mysqlUser;

    @Value("${MYSQL_PASSWORD:workflow_password}")
    private String mysqlPassword;

    @Value("${MYSQL_DATABASE:workflow_db}")
    private String mysqlDatabase;

    // ===== Redis Settings =====
    @Value("${REDIS_HOST:redis}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private Integer redisPort;

    // ===== Qdrant Settings =====
    @Value("${QDRANT_HOST:qdrant}")
    private String qdrantHost;

    @Value("${QDRANT_PORT:6333}")
    private Integer qdrantPort;

    @Value("${QDRANT_URL:http://qdrant:6333}")
    private String qdrantUrl;

    @Value("${QDRANT_PREFER_GRPC:true}")
    private Boolean qdrantPreferGrpc;

    // ===== MinIO Settings =====
    @Value("${MINIO_ENDPOINT:minio:9000}")
    private String minioEndpoint;

    @Value("${MINIO_ACCESS_KEY:minioadmin}")
    private String minioAccessKey;

    @Value("${MINIO_SECRET_KEY:minioadmin}")
    private String minioSecretKey;

    // ===== Celery Settings =====
    @Value("${CELERY_BROKER_URL:redis://redis:6379/0}")
    private String celeryBrokerUrl;

    @Value("${CELERY_RESULT_BACKEND:redis://redis:6379/0}")
    private String celeryResultBackend;

    @Value("${CELERY_TASK_SERIALIZER:json}")
    private String celeryTaskSerializer;

    @Value("${CELERY_RESULT_SERIALIZER:json}")
    private String celeryResultSerializer;

    @Value("${CELERY_ACCEPT_CONTENT:json}")
    private String celeryAcceptContent;

    @Value("${CELERY_TIMEZONE:UTC}")
    private String celeryTimezone;

    @Value("${CELERY_ENABLE_UTC:true}")
    private Boolean celeryEnableUtc;

    // ===== Task Settings =====
    @Value("${MAX_CONCURRENT_TASKS_PER_USER:20}")
    private Integer maxConcurrentTasksPerUser;

    @Value("${MAX_DAILY_TASKS_PER_USER:100}")
    private Integer maxDailyTasksPerUser;

    @Value("${NUM_THREADS_FOR_DOC_PROCESSING:4}")
    private Integer numThreadsForDocProcessing;

    // ===== Security Settings =====
    @Value("${SECRET_KEY:your-secret-key-here}")
    private String secretKey;

    @Value("${ALGORITHM:HS256}")
    private String algorithm;

    // ===== Token Settings =====
    @Value("${ACCESS_TOKEN_EXPIRE_MINUTES:60}")
    private Integer accessTokenExpireMinutes;

    @Value("${REFRESH_TOKEN_EXPIRE_DAYS:7}")
    private Integer refreshTokenExpireDays;

    @Value("${USER_NO_INTERACT_EXPIRE_SECONDS:3600}")
    private Integer userNoInteractExpireSeconds;

    // ===== Environment Settings =====
    @Value("${ENVIRONMENT:development}")
    private String environment;

    @Value("${DEBUG:true}")
    private Boolean debug;

    @Value("${TZ:Asia/Ho_Chi_Minh}")
    private String tz;

    // ===== Custom getter methods =====
    public String getCollectionName() {
        return "default_collection";
    }

    public String getEmbeddingsMethod() {
        return "dense";
    }
}