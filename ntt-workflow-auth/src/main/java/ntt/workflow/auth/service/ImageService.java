package ntt.workflow.auth.service;
import ntt.workflow.auth.entity.CmsImage;
import ntt.common.jdbc.repository.IJdbcRepository;
import ntt.workflow.auth.model.ImageResponse;
import ntt.workflow.auth.model.SaveImageRequest;
import ntt.workflow.auth.repository.ImageRepository;
import ntt.workflow.auth.utils.ImageUtil;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private IJdbcRepository jdbcRepository;
    @Autowired
    private FileService fileService;

    public CmsImage saveImage(SaveImageRequest request){
        CmsImage cmsImage = new CmsImage();
        cmsImage.setId(request.getId());
        cmsImage.setName(request.getName());
        cmsImage.setType(request.getType());
        cmsImage.setCategory(request.getCategory());
        cmsImage.setPath(request.getPath());
        cmsImage.setUserId(request.getUserId());
        cmsImage.setUri(request.getUri());
        cmsImage.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        cmsImage.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        //Save image data in file
        return imageRepository.save(cmsImage);
    }

    public CmsImage uploadImage(MultipartFile file) throws IOException {
        CmsImage cmsImage = new CmsImage();
        cmsImage.setName(file.getOriginalFilename());
        cmsImage.setType(file.getContentType());
        if(file.getBytes() != null){
            cmsImage.setData(ImageUtil.compressImage(file.getBytes()));
        }
        return imageRepository.save(cmsImage);
    }

    @Transactional
    public CmsImage getDbImage(String id) {
        Optional<CmsImage> dbImage = imageRepository.findById(id);
        byte[] decompressImage = ImageUtil.decompressImage(dbImage.get().getData());
        return CmsImage.builder()
                .name(dbImage.get().getName())
                .type(dbImage.get().getType())
                .data(decompressImage).build();
    }

    public CmsImage getImage(String id){
        return this.imageRepository.getById(id);
    }

    public String getImagePath(String category, String imageId, String type){
        if(StringUtils.isEmpty(imageId)) return null;
        Path path = this.fileService.initCategoryDirectory(category);
        String filePath = path.toString() + "/" + imageId + "." + type;
        return filePath;
    }

    public ImageResponse getImageById(String id) throws IOException {
        CmsImage image = this.imageRepository.getById(id);
        String filePath = this.getImagePath(image.getCategory(), image.getId(), image.getType());
        File fi = new File(filePath);
        byte[] data = Files.readAllBytes(fi.toPath());
        String mediaType = "image/" + image.getType();

        ImageResponse response = new ImageResponse();
        response.setData(data);
        response.setId(image.getId());
        response.setCategory(image.getCategory());
        response.setType(image.getType());
        response.setPath(image.getPath());
        response.setMediaType(mediaType);
        return response;
    }

    @Transactional
    public byte[] getImageAsBytes(String id) {
        Optional<CmsImage> dbImage = imageRepository.findById(id);
        if(!dbImage.isPresent()) return null;
        byte[] image = ImageUtil.decompressImage(dbImage.get().getData());
        return image;
    }

    public List<ImageResponse> getImages(String category){
        String query = null;
        if(StringUtils.isEmpty(category)){
            query = "select * from cms_image";
        } else {
            query = String.format("select * from cms_image img where img.category='%s'", category);
        }
        List<ImageResponse> list = this.jdbcRepository.queryForList(ImageResponse.class, query);
        return list;
    }

    public void deleteImage(String id){
        this.imageRepository.deleteById(id);
    }

    public void deleteImageCategory(String category){
        List<CmsImage> list = this.imageRepository.findByCategory(category);
        if(list == null) return;
        for(CmsImage img: list) {
            String id = img.getId();
            this.imageRepository.deleteById(id);
        }
    }

    public void deleteAllImages(){
        List<CmsImage> list = imageRepository.findAll();
        if(list == null) return;
        for(CmsImage img: list) {
            String id = img.getId();
            this.imageRepository.deleteById(id);
        }
    }

}