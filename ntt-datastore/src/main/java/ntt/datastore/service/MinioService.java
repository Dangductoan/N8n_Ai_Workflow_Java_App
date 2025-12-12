/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-13
 * Description : Create MinIO Storage Service
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.datastore.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import ntt.common.api.shared.tenant.dto.UserSpacesInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.minio.PutObjectArgs;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);


    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public Boolean createBucketIfNotExists(String bucketName) throws Exception {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                logger.info("Creating bucket: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                return true;
            } else {
                logger.info("Bucket already exists: {}", bucketName);
            }
            return true;
        }catch (Exception ex){
            throw ex;
        }
    }

    /**
     * Create MinIO bucket for a specific company.
     *
     * @param bucketName   Bucket Name
     * @return             The created or existing bucket name
     */
    public String createCompanyBucket( String bucketName) {
        try {
            // ✅ Check if bucket already exists
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                logger.info("Created MinIO bucket: {}", bucketName);
            } else {
                logger.info("MinIO bucket already exists: {}", bucketName);
            }
            return bucketName;

        }
        catch (Exception e) {
            // Include stack trace in logs and wrap the exception
            logger.error("Failed to create MinIO bucket {}: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("Failed to create MinIO bucket: " + bucketName, e);
        }
        }



        /**
         * Delete MinIO bucket for a specific company.
         *
         * @param bucketName  Name of the company
         * @return             The bucket name that was deleted (or attempted)
         */
    public String deleteCompanyBucket( String bucketName) {
        try {

            // ✅ Check if bucket exists
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (exists) {
                // ✅ Delete all objects in bucket
                this.deleteObjectsInBucketByPrefix(bucketName,"");

                // ✅ Remove the bucket itself
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
                logger.info("Deleted MinIO bucket: {}", bucketName);
            } else {
                logger.info("MinIO bucket does not exist: {}", bucketName);
            }

            return bucketName;

        } catch (Exception e) {
            logger.error("Error deleting company bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete company bucket", e);
        }
    }

    /**
     * Generate a presigned URL for a MinIO object.
     *
     * @param bucketName     Name of the MinIO bucket
     * @param objectName     Name/path of the object in the bucket
     * @param expiresSeconds Expiration time in seconds (default: 12 hours)
     * @return Presigned URL string
     */
    public String generatePresignedUrl(String bucketName, String objectName, int expiresSeconds) {
        try {
            // Generate presigned GET URL
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiresSeconds, TimeUnit.SECONDS)
                            .build()
            );


            logger.info("Generated presigned URL for {}/{}", bucketName, objectName);
            return url;

        } catch (MinioException e) {
            logger.error("Failed to generate presigned URL for {}/{}: {}", bucketName, objectName, e.getMessage());
            throw new RuntimeException("MinIO error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    // Overloaded method with default 12 hours expiry
    public String generatePresignedUrl(String bucketName, String objectName) {
        return generatePresignedUrl(bucketName, objectName, 43200);
    }

    public void putObject(String bucketName, List<String> folders) {

            for (String folderPath : folders) {
                try {
                    String emptyObjectName = folderPath + ".keep";
                    ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
                    this.minioClient.putObject( PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(emptyObjectName)
                            .stream(emptyStream, 0, -1)
                            .build());
                    logger.info("Created folder: {}/{}",bucketName,folderPath);
                } catch (Exception e) {
                    logger.warn("Failed to create folder {}/{}: {}", bucketName, folderPath, e.getMessage());
                }

            }


    }


    /**
     * # Delete objects in company bucket by prefix
     *
     * @param bucketName     bucketName
     * @param prefix         prefix
     * @return Presigned UserSpacesInfoDto
     */
    public UserSpacesInfoDto deleteObjectsInBucketByPrefix(String bucketName, String prefix) {

        UserSpacesInfoDto userSpacesInfoDto = new UserSpacesInfoDto();
        int count = 0;
        try {


            logger.info("Deleting objects in bucket: {}", bucketName);

            // ✅ List and delete all objects with prefix (if prefix = "" will get alls)
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : objects) {

                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
                count++;
                logger.info("Deleted object: {}", item.objectName());
            }


           // Set output userSpaceInfoDto
            userSpacesInfoDto.setCompanyBucket(bucketName);
            userSpacesInfoDto.setDeletedUserObjectsCount(count);



        }catch (Exception e) {
            logger.error("Error deleting object : {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deleting object", e);
        }

        return  userSpacesInfoDto;
    }

    public String findBucketNameByUuid(String uuid) {
        String bucketPrefix = String.format("company-%s-", uuid);

        try {
            //get all bucket list
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket :  bucketList ) {
                if (bucket.name().startsWith(bucketPrefix)) {
                    logger.info("Bucket exists with prefix '{}': {}", bucketPrefix, bucket.name());
                    return bucket.name();
                }
            }
            logger.info("No bucket found with prefix '{}'", bucketPrefix);
            return  null;
        } catch (Exception e) {
            logger.error("Failed to check bucket existence for uuid {}: {}", uuid, e.getMessage(), e);
            return  null;
        }
    }


}

