//package cms.common.jpa.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import jakarta.persistence.MappedSuperclass;
//import org.hibernate.annotations.GenericGenerator;
//import org.hibernate.annotations.Type;
//
//import java.io.Serializable;
//import java.sql.Timestamp;
//import java.util.UUID;
//
//@MappedSuperclass
//public class AbstractBaseEntityUUID implements Serializable {
//
//    @Id
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
//
//    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
//    //Using “uuid-char” instructs Hibernate to store the UUID as a String, instead of the binary value
//    //This way, it can be written to and read from a VARCHAR(36) column without the need for any pre-processing on our side
//    @Type(type = "uuid-char")
//    private UUID id;
//    private Timestamp createdAt;
//    private Timestamp updatedAt;
//    private Timestamp deletedAt;
//
//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public Timestamp getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Timestamp createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Timestamp getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Timestamp updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public Timestamp getDeletedAt() {
//        return deletedAt;
//    }
//
//    public void setDeletedAt(Timestamp deletedAt) {
//        this.deletedAt = deletedAt;
//    }
//
//    public AbstractBaseEntityUUID() {
//    }
//
//}
