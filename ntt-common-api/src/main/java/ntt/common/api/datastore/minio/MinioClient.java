/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-14
 * Description : Create common MinIO Client
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.common.api.datastore.minio;

import ntt.common.api.shared.tenant.dto.UserSpacesInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


//If you don't need LoadBalancer just only want to call static URL:
@FeignClient(
        name = "minio-storage",
        url = "${clients.storage.minio.url}"
)
public interface MinioClient {

    //createBucket
    @PostMapping("/api/minio/create-bucket/{name}")
    String createBucket(@PathVariable("name") String name);

    //deleteBucket
    @DeleteMapping("/api/minio/delete-bucket/{name}")
    String deleteBucket(@PathVariable("name") String name);

    //generatePresignedUrl
    @PostMapping("/api/minio/generate-presigned-url/{bucketName}/{objectName}/{expiresSeconds}")
    String generatePresignedUrl(@PathVariable("bucketName") String bucketName,
                                @PathVariable("objectName") String objectName,
                                @PathVariable("expiresSeconds") String expiresSeconds);

    //putObject
    @PostMapping("api/minio/put-object-to-bucket/{bucketName}")
    public void putObject(@PathVariable(value="bucketName") String bucketName,@RequestBody String[] folders );

    //delete_objects_in_bucket_by_prefix
    @DeleteMapping("api/minio/delete-object-in-bucket-by-prefix/{bucketName}/{prefix}")
    UserSpacesInfoDto deleteObjectsInBucketByPrefix(@PathVariable(value="bucketName") String bucketName,
                                                           @PathVariable(value="prefix") String prefix);
    //check bucket exist
    @PostMapping("api/minio/check-bucket-exist/{uuid}")
    String checkBucketExist(@PathVariable(value="uuid") String uuid);



}
