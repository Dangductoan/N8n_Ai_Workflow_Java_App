package ntt.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, unique = true, nullable = false)
    private String uuid;

    @Column(name = "parent_uuid", length = 255)
    private String parentUuid;

    @Column(length = 255, unique = true, nullable = false)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(name = "node_pos_x")
    private Integer nodePosX;

    @Column(name = "node_pos_y")
    private Integer nodePosY;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Company company;

    @Builder.Default
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}