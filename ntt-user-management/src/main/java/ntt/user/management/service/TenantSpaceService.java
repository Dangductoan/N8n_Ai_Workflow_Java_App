package ntt.user.management.service;

import lombok.extern.slf4j.Slf4j;
import ntt.common.api.shared.tenant.dto.*;
import org.springframework.stereotype.Service;
import ntt.common.api.datastore.minio.MinioClient;
import ntt.common.api.datastore.qdrant.QdrantClient;

import java.util.List;

@Slf4j
@Service
public class TenantSpaceService {

    private final MinioClient minioClient;
    private final QdrantClient qdrantClient;
    private  final CompanyService companyService;

    public TenantSpaceService(MinioClient minioClient, QdrantClient qdrantClient, CompanyService companyService) {
        this.minioClient = minioClient;
        this.qdrantClient = qdrantClient;
        this.companyService = companyService;
    }

    /**
     * Sanitize tên để tạo bucket/collection name an toàn
     * S3 bucket naming rules:
     * - 3-63 characters long
     * - Lowercase letters, numbers, dots, and hyphens only
     * - Must start and end with a letter or number
     */
    private String sanitizeName(String name) {
        // Convert to lowercase and replace invalid characters with hyphens
        String sanitized = name.toLowerCase().replaceAll("[^a-z0-9\\-]", "-");
        // Replace multiple hyphens with single hyphen
        sanitized = sanitized.replaceAll("-+", "-");
        // Remove hyphens from start and end
        sanitized = sanitized.replaceAll("^-+|-+$", "");
        // Ensure it starts with a letter or number
        if (!sanitized.isEmpty() && !Character.isLetterOrDigit(sanitized.charAt(0))) {
            sanitized = "c" + sanitized;
        }
        // Ensure it ends with a letter or number
        if (!sanitized.isEmpty() && !Character.isLetterOrDigit(sanitized.charAt(sanitized.length() - 1))) {
            sanitized = sanitized + "0";
        }
        // Limit length to 63 characters
        if (sanitized.length() > 63) {
            sanitized = sanitized.substring(0, 63);
            // Ensure it ends with alphanumeric
            if (!Character.isLetterOrDigit(sanitized.charAt(sanitized.length() - 1))) {
                sanitized = sanitized.substring(0, 62) + "0";
            }
        }
        return sanitized;
    }

    /**
     * Create company spaces
     */
    public CompanySpaceDto createCompanySpaces(String companyUuid, String companyName) {
        try {
            log.info("Starting to create spaces for company {} (UUID: {})", companyName, companyUuid);
            String sanitizedName = sanitizeName(companyName);
            log.info("Sanitized company name: {}", sanitizedName);

            // 1. Tạo MinIO bucket (S3 compliant naming)
            String minioBucketName = String.format("company-%s-%s", companyUuid, sanitizedName);
            log.info("Creating MinIO bucket: {}", minioBucketName);
            minioClient.createBucket(minioBucketName);
            log.info("Successfully created MinIO bucket: {}", minioBucketName);

            // 2. Tạo Qdrant collection (can use underscores)
            String qdrantCollectionName = String.format("company_%s_%s", companyUuid, sanitizedName);
            log.info("Creating Qdrant collection: {}", qdrantCollectionName);
            qdrantClient.createQdrantCollection(qdrantCollectionName);
            log.info("Successfully created Qdrant collection: {}", qdrantCollectionName);

            // 3. MySQL database name (cho tương lai nếu cần)
            String mysqlDatabaseName = String.format("company_%s_%s", companyUuid, sanitizedName);

            // 4. Tạo folder structure trong company bucket
            log.info("Creating folder structure in MinIO bucket: {}", minioBucketName);
            createCompanyFolderStructure(minioBucketName);
            log.info("Successfully created folder structure in MinIO bucket: {}", minioBucketName);

            CompanySpaceDto spacesInfo = new CompanySpaceDto(minioBucketName,qdrantCollectionName,mysqlDatabaseName);

            log.info("Successfully created all spaces for company {} (UUID: {}): {}",
                    companyName, companyUuid, spacesInfo);
            return spacesInfo;

        } catch (Exception e) {
            log.error("Failed to create spaces for company {} (UUID: {}): {}",
                    companyName, companyUuid, e.getMessage(), e);
            throw new RuntimeException("Failed to create company spaces", e);
        }
    }

    /*
     * Delete Company space
     */
    public DeleteCompanySpaceDto deleteCompanySpace(String companyUuid, String companyName) {

        try {
            String sanitizedName = sanitizeName(companyName);

            // 1. Xóa MinIO bucket (S3 compliant naming)
            String minioBucketName = String.format("company-%s-%s", companyUuid, sanitizedName);
            minioClient.deleteBucket(minioBucketName);

            // 2. Xóa Qdrant collection (dùng underscore)
            String qdrantCollectionName = String.format("company_%s_%s", companyUuid, sanitizedName);
            qdrantClient.deleteQdrantCollection(qdrantCollectionName);

            // 3. Xóa tất cả project collections trong Qdrant
            qdrantClient.deleteCompanyProjectCollection(companyUuid);

            // 4. Xóa tất cả user spaces trong company
            UserSpacesInfoDto userSpacesInfoDto = this.deleteCompanyUserSpaces(companyUuid);

            // 5. MySQL database name (cho tương lai nếu cần)
            String mysqlDatabaseName = String.format("company_%s_%s", companyUuid, sanitizedName);

            DeleteCompanySpaceDto deleteCompanySpaceDto =
                    new DeleteCompanySpaceDto(minioBucketName,qdrantCollectionName,mysqlDatabaseName,Boolean.TRUE,userSpacesInfoDto);

            log.info("Deleted all spaces for company {} (UUID: {}): {}", companyName, companyUuid,userSpacesInfoDto);
            return deleteCompanySpaceDto;
        } catch (Exception e) {
            log.error("Failed to delete spaces for company {} (UUID: {}): {}", companyName, companyUuid, e.getMessage(), e);
            throw new RuntimeException("Failed to delete spaces for company " + companyName + " (UUID: " + companyUuid + ")", e);
        }


    }

    /*
     * Delete company users spaces
     */

      public UserSpacesInfoDto deleteCompanyUserSpaces(String uuid) {
          CompanySpaceDto companySpaceDto = this.getCompanySpaceInfo(uuid,"company");
          UserSpacesInfoDto userSpacesInfoDto = new UserSpacesInfoDto();
          String companyBucket = companySpaceDto.getMinioBucketName();

          //Delete all user folders in company bucket
          try {
               userSpacesInfoDto =  minioClient.deleteObjectsInBucketByPrefix(companyBucket,"users");
              log.info("Deleted user spaces for company {}: {}",uuid, userSpacesInfoDto);
;          }catch (Exception e) {
              log.error("Failed to delete user spaces for company {}: {}",uuid,e.getMessage());
          }
          return userSpacesInfoDto;
      }

    /*
     * Function used in createCompanySpaces
     */
    public void createCompanyFolderStructure(String bucketName) {
        String[] folders = new String[] {

                // Knowledge Base folders
                "knowledge-bases/",

                // Project folders
                "projects/",

                // Workflow folders
                "workflows/",

                // Agent folders
                "agents/",

                // Users folders
                "users/",

                // Logs folders
                "logs/"

        };
        try {
                    minioClient.putObject(bucketName,folders);
                    log.info("Created company folder: {}", bucketName);


        }catch (Exception e) {

            log.error("Failed to create company folder structure for {}: {}", bucketName, e.getMessage());
            // Re-throw so callers can handle/report higher-level failure
            throw new RuntimeException("Failed to create company folder structure for " + bucketName, e);

        }
    }
    /*
     * Function getCompanySpaceInfo
     *
     */
    public CompanySpaceDto getCompanySpaceInfo(String uuid, String companyName) {

        String sanitizedName = this.sanitizeName(companyName);


        String minioBucketName       = String.format("company-%s-%s", uuid, sanitizedName);
        String qdrantCollectionName  = String.format("company_%s_%s", uuid, sanitizedName);
        String mysqlDatabaseName     = String.format("company_%s_%s", uuid, sanitizedName);


        return new CompanySpaceDto(minioBucketName,qdrantCollectionName,mysqlDatabaseName);
    }
    /*
     * Function: createUserSpaces
     * @param: String uuid, int userId
     * @return:  UserSpacesDto
     */
    public UserSpacesDto createUserSpaces(String uuid, int userId ) {
        String companyBucket = null;

        try {
            //Lấy từ DB
            CompanyDto companyDto = companyService.getCompanyByUuid(uuid);
            CompanySpaceDto companySpaceDto = this.getCompanySpaceInfo(uuid,companyDto.getName());
             companyBucket = companySpaceDto.getMinioBucketName();

             //Fallback nếu chưa có
            if (companyBucket == null || companyBucket.isEmpty()) {
                companyBucket = this.minioClient.checkBucketExist(uuid);
            }

            if (companyBucket == null || companyBucket.isEmpty()) {

                throw new RuntimeException("Company bucket not found for company UUID:" + uuid);

            }
            String[] userFolders = new String[] {
                    String.format("users/%d/", userId),
                    String.format("users/%d/avatars/", userId)
            };
            this.minioClient.putObject(companyBucket,userFolders);

            return  new UserSpacesDto(companyBucket, List.of(userFolders));

        }catch (Exception e) {
            log.error("Failed to create user spaces for user {} in company {}: {}",userId,uuid,e.getMessage(),e);
            throw e;
        }


    }

    /*
     * Function: createProjectSpaces
     * @param: ProjectSpacesRequestDto rq
     * @return:  ProjectSpacesDto
     */
    public ProjectSpacesDto createProjectSpaces(ProjectSpacesRequestDto rq) {
       try {
           //Get Company Info
           String companyName = rq.getCompanyName() != null ? rq.getCompanyName() : "company";
           CompanySpaceDto companyInfo =  this.getCompanySpaceInfo(rq.getCompanyUUID(),companyName);
           String companyBucket = companyInfo.getMinioBucketName();

           // Create project identifier using project_id for consistent naming
           String projectIdentifier = rq.getProjectId() != null
                   ? String.valueOf(rq.getProjectId()) : rq.getProjectUUID();

           // Create project folders structure
           String[] projectFolders = new String[]{
                   String.format("projects/%s/", projectIdentifier),
                   String.format("projects/%s/knowledge-bases/", projectIdentifier),
                   String.format("projects/%s/workflows/", projectIdentifier),
                   String.format("projects/%s/workflows/executions/", projectIdentifier),
                   String.format("projects/%s/workflows/templates/", projectIdentifier),
                   String.format("projects/%s/shared-assets/", projectIdentifier),
                   String.format("projects/%s/exports/", projectIdentifier)
           };

           //put folder in bucket
           this.minioClient.putObject(companyBucket,projectFolders);
           //Use company's Qdrant collection instead of creating separate one
           String qdrantCollectionName = companyInfo.getQdrantCollectionName();
           ProjectSpacesDto projectSpacesDto =
                   new ProjectSpacesDto(companyBucket,qdrantCollectionName,rq.getProjectUUID(),rq.getProjectId());

           log.info("Created project spaces for {} (UUID: {}, ID: {}): {}",
                   rq.getProjectName(), rq.getProjectUUID(), rq.getProjectId(), projectSpacesDto);
           return projectSpacesDto;

       } catch (Exception e) {
           log.error("Failed to create project spaces for {} (UUID: {}): {}",
                   rq.getProjectName(),  rq.getProjectUUID(), e.getMessage());
           throw new RuntimeException("Failed to create project spaces", e);
       }


    }
}
