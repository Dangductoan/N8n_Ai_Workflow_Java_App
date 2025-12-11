package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid;

    @Column(length = 255, unique = true, nullable = false)
    private String name;

    @Column(name = "node_pos_x")
    private Integer nodePosX;

    @Column(name = "node_pos_y")
    private Integer nodePosY;

    @Column(name = "minio_bucket_name", length = 255, unique = true)
    private String minioBucketName;

    @Column(name = "qdrant_collection_name", length = 255, unique = true)
    private String qdrantCollectionName;

    @Column(name = "mysql_database_name", length = 255, unique = true)
    private String mysqlDatabaseName;

    // Relationships
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private List<OrganizationUnit> organizationUnits = new ArrayList<>();
}