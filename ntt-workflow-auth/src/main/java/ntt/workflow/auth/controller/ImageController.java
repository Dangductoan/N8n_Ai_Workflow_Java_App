package ntt.workflow.auth.controller;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import ntt.workflow.auth.model.ImageResponse;
import ntt.workflow.auth.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//https://frontbackend.com/spring-boot/spring-boot-upload-file-to-filesystem

@RestController
@RequestMapping("/cms-media-api/image")
//@ShenyuSpringMvcClient("/api/image/**")
public class ImageController {

    @Autowired
    private MediaService mediaService;

    @GetMapping("/test")
    public Object test() {
        LocalDateTime now = LocalDateTime.now();
        return "Hello world " + now.toString();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> upload(@RequestPart("file") MultipartFile file){
        return this.upload(file, null);
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> uploadForId(@RequestPart("file") MultipartFile file, @PathVariable(value="id") String id){
        return this.mediaService.upload(file, null, id);
    }

    @PostMapping(value = "/category/upload", consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> upload(@RequestPart("file") MultipartFile file, @RequestParam(value="category") String category) {
        return this.mediaService.upload(file, category, null);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable(value="id") String id) throws IOException {
        return this.mediaService.getImage(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ImageResponse>> getImages() {
        return this.getImages(null);
    }

    @GetMapping("/category/list")
    public ResponseEntity<List<ImageResponse>> getImages(@RequestParam(value = "category") String category) {
        return this.mediaService.getImages(category);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable(value="id") String id) {
        return this.mediaService.delete(id);
    }

    @DeleteMapping("/category/delete")
    public void deleteCategory(@RequestParam(value = "category") String category) {
        this.mediaService.deleteCategory(category);
    }

    @DeleteMapping("/deleteAll")
    public void deleteAll() {
        this.mediaService.deleteAll();
    }
}
