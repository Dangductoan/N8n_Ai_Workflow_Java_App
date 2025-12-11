package ntt.workflow.auth.interfaces;

import ntt.workflow.auth.configuration.FeignSupportConfig;
import ntt.workflow.auth.model.ImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(
        name = "auth-service",
        url = "${clients.media.url:http://localhost:8090}",
        configuration = FeignSupportConfig.class)

////For K8s
//@FeignClient(
//        name = "auth-media",
//        url = "${clients.media.url}",
//        path = "/api/image"
//)

public interface MediaClient {

    @PostMapping(value = "/api/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ImageResponse> upload(@RequestPart("file") MultipartFile file);

    @PostMapping(value = "/api/image/upload/{category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ImageResponse> upload(@RequestPart("file") MultipartFile file, @PathVariable(value = "category") String category);

    @ResponseBody
    @GetMapping("/api/image/{id}")
    ResponseEntity<byte[]> getImage(@PathVariable(value = "id") String id);

    @GetMapping("/api/image/list")
    ResponseEntity<List<ImageResponse>> getImages();

    @GetMapping("/api/image/list/{category}")
    ResponseEntity<List<ImageResponse>> getImages(@PathVariable(value = "category") String category);

    @DeleteMapping("/api/image/delete/{id}")
    Boolean delete(@PathVariable(value = "id") String id);

    @DeleteMapping("/api/image/deleteCategory/{category}")
    void deleteCategory(@PathVariable(value = "category") String category);

    @DeleteMapping("/api/image/deleteAll")
    void deleteAll();

}
