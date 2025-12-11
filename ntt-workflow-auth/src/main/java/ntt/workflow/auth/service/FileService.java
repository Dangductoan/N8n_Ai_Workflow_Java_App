package ntt.workflow.auth.service;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import ntt.workflow.auth.entity.CmsImage;
import ntt.workflow.auth.model.SaveImageResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    public static String ROOT_DIRECTORY = System.getProperty("user.dir");
    public static String MEDIA_DIRECTORY = "media";

    public Path initCategoryDirectory(String category) {
        try {
            String directory = ROOT_DIRECTORY + "/" + MEDIA_DIRECTORY;
            if(!StringUtils.isEmpty(category)) {
                directory = directory + "/" + category;
            }
            Path userPath = Paths.get(directory);
            if (!Files.exists(userPath)) {
                Files.createDirectories(Paths.get(directory));
            }
            return userPath;
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public SaveImageResponse save(String category, MultipartFile file, String id) {
        try {
            Path catPath = this.initCategoryDirectory(category);

            Optional<String> optExtension = this.getExtension(file.getOriginalFilename());
            String extension = optExtension.isPresent()? optExtension.get(): null;
            String imageId = StringUtils.isEmpty(id)?  UUID.randomUUID().toString(): id;
            //Make sure not use temporary Id
            if(imageId.startsWith("_")){
                imageId = imageId.substring(1);
            }
            String image = imageId + "." + extension;
            Files.copy(file.getInputStream(),
                    catPath.resolve(image),
                    StandardCopyOption.REPLACE_EXISTING
            );
            SaveImageResponse response = new SaveImageResponse();
            response.setId(imageId);
            response.setType(extension);
            response.setCategory(category);
            response.setPath(MEDIA_DIRECTORY);

            return  response;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Resource load(String category, String fileName) {
        try {
            Path catPath = this.initCategoryDirectory(category);
            Path file = catPath.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public boolean deleteAll() {
        Path path = this.initCategoryDirectory(null);
        boolean result = FileSystemUtils.deleteRecursively(Paths.get(path.toString()).toFile());
        return result;
    }

    public boolean delete(String filePath){
        if (!Files.exists(Path.of(filePath))) {
            return false;
        }
        File file = new File(filePath);
        return file.delete();
    }

    public boolean deleteCategory(String category){
        if(StringUtils.isEmpty(category)) return false;
        String directory = ROOT_DIRECTORY + "/" + MEDIA_DIRECTORY + "/" + category;
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            return false;
        }
        boolean result = FileSystemUtils.deleteRecursively(Paths.get(path.toString()).toFile());
        return result;
    }

    public boolean delete(CmsImage cmsImage){
        try {
            Path directory = this.initCategoryDirectory(null);
            String filePath = directory.toString() + "/" + cmsImage.getId() + "." + cmsImage.getType();
            Path path = Paths.get(filePath);
            boolean result = Files.deleteIfExists(path);
            return result;
        }catch (Exception ex){
            return false;
        }
    }

    public List<Path> loadCategory(String category) {
        try {
            Path catPath = this.initCategoryDirectory(category);
            if (Files.exists(catPath)) {
                return Files.walk(catPath, 1)
                        .filter(path -> !path.equals(catPath))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }
}