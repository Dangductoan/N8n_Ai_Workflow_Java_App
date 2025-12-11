package ntt.testing.service;

import lombok.AllArgsConstructor;
import ntt.common.api.datastore.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TestingMinioService {
    MinioClient minioClient;

    public String testCreatingBucket(String uuid, String bucketName) {
         return this.minioClient.createBucket(bucketName);
    }

    public String testDeletingBucket(String uuid, String bucketName) {
        return this.minioClient.deleteBucket(bucketName);
    }

}
