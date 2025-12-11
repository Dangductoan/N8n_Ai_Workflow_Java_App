package ntt.workflow.auth.service;

import ntt.workflow.auth.entity.CmsImage;
import ntt.workflow.auth.model.SaveImageRequest;
import ntt.workflow.auth.model.SaveImageResponse;
import ntt.workflow.auth.model.ImageResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MediaService {

    @Autowired
    FileService fileService;

    @Autowired
    ImageService imageService;

    public ResponseEntity<ImageResponse> upload(MultipartFile file, String category, String id) {
        try {
            SaveImageResponse imgFile = fileService.save(category, file, id);
            String fileId = imgFile.getId();
            String imageURI = String.format("/cms-media-api/image/%s", fileId);

            SaveImageRequest imgRequest = new SaveImageRequest();
            imgRequest.setId(fileId);
            imgRequest.setName(file.getOriginalFilename());
            imgRequest.setPath(imgFile.getPath());
            imgRequest.setType(imgFile.getType());
            imgRequest.setCategory(category);
            imgRequest.setUri(imageURI);
            CmsImage cmsImage = this.imageService.saveImage(imgRequest);

            ImageResponse response = new ImageResponse();
            response.setId(imgRequest.getId());
            response.setName(imgRequest.getName());
            response.setPath(imgRequest.getPath());
            response.setType(imgRequest.getType());
            response.setCategory(imgRequest.getCategory());
            response.setUri(imageURI);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception ex) {
            ImageResponse exResponse = new ImageResponse();
            exResponse.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(exResponse);
        }
    }

    public ResponseEntity<ImageResponse> upload(@RequestPart("file") MultipartFile file){
        return this.upload(file, null, null);
    }

    public ResponseEntity<byte[]> getImage(@RequestParam(value="id") String id) throws IOException {
        ImageResponse response = this.imageService.getImageById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(response.getMediaType()))
                .body(response.getData());
    }
    public ResponseEntity<List<ImageResponse>> getImages() {
        return this.getImages(null);
    }

    public ResponseEntity<List<ImageResponse>> getImages(@RequestParam(value = "category") String category) {
        List<ImageResponse> listImage = this.imageService.getImages(category);
        return ResponseEntity.status(HttpStatus.OK).body(listImage);
    }

    public Boolean delete(@RequestParam(value="id") String id) {
        CmsImage image = this.imageService.getImage(id);
        if(image == null) return false;
        String filePath = this.imageService.getImagePath(image.getCategory(), image.getId(), image.getType());
        if(!StringUtils.isEmpty(filePath)) {
            this.imageService.deleteImage(id);
            this.fileService.delete(filePath);
        }
        return true;
    }

    public void deleteCategory(@RequestParam(value = "category") String category) {
        boolean deleted = fileService.delete(category);
        if(deleted){
            this.imageService.deleteImageCategory(category);
        }
    }
    public void deleteAll() {
        boolean deleted = fileService.deleteAll();
        if(deleted){
            this.imageService.deleteAllImages();
        }
    }

}
