package ntt.workflow.auth.repository;

import ntt.workflow.auth.entity.CmsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//Without @Repository, this interface could not be injected to service class
@Repository
public interface ImageRepository extends JpaRepository<CmsImage, String> {
    @Query("select img from CmsImage img where img.category = :category")
    List<CmsImage> findByCategory(@Param("category") String category);
}
