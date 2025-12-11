package ntt.datastore.controller;

import ntt.common.api.shared.tenant.dto.UserSpacesInfoDto;
import ntt.datastore.service.MinioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/minio")
@CrossOrigin(origins = "*")
public class MinioController {

	private final MinioService minioService;

	public MinioController(MinioService minioService){
		this.minioService = minioService;
	}

    @PostMapping("/create-bucket/{name}")
    //This is not a view, so this is not passed through ViewResolver causing duplicate
	public String createBucket( @PathVariable(value="name") String name) {
		return this.minioService.createCompanyBucket(name);
	}

    @DeleteMapping("/delete-bucket/{name}")
    public String deleteBucket(@PathVariable(value="name") String name) {
        return this.minioService.deleteCompanyBucket(name);
    }

    @PostMapping("/generate-presigned-url/{bucketName}/{objectName}/{expiresSeconds}")
    public String generatePresignedUrl(@PathVariable(value="bucketName") String bucketName,
                                                       @PathVariable(value="objectName") String objectName,
                                                       @PathVariable(value="expiresSeconds") String expiresSeconds
                                                       ) {
        return this.minioService.generatePresignedUrl(bucketName, objectName, Integer.parseInt(expiresSeconds));
    }

    @PostMapping("/put-object-to-bucket/{bucketName}")
    public void putObject(@PathVariable(value="bucketName") String bucketName,@RequestBody List<String> folders) {
         this.minioService.putObject(bucketName,folders);
    }

    @DeleteMapping("/delete-object-in-bucket-by-prefix/{bucketName}/{prefix}")
    public UserSpacesInfoDto deleteObjectsInBucketByPrefix(@PathVariable(value="bucketName") String bucketName,
                                                           @PathVariable(value="prefix") String prefix) {
        return this.minioService.deleteObjectsInBucketByPrefix(bucketName,prefix);
    }

    @PostMapping("api/minio/check-bucket-exist/{uuid}")
    String checkBucketExist(@PathVariable(value="uuid") String uuid) {
        return this.minioService.checkBucketExist(uuid);
    }




}
