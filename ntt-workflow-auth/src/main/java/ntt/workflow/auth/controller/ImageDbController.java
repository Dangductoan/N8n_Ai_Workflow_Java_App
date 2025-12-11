package ntt.workflow.auth.controller;

import ntt.workflow.auth.entity.CmsImage;
import ntt.workflow.auth.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/cms-media-api/imagedb")
public class ImageDbController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        CmsImage response = imageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?>  getImageInfo(@RequestPart("id") String id){
        CmsImage cmsImage = imageService.getImage(id);
        return ResponseEntity.status(HttpStatus.OK).body(cmsImage);
    }

    @GetMapping("image")
    public ResponseEntity<?>  getImage(@RequestParam("id") String id){
        byte[] image = imageService.getImageAsBytes(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

}
