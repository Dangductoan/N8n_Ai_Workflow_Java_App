package ntt.user.management.entity;


import lombok.Data;
import jakarta.persistence.*;


@Data
@Entity
@Table(name = "chat_knowledge_bases")
@IdClass(ChatKnowledgeBaseId.class)
public class ChatKnowledgeBase {

    @Id
    @Column(name = "chat_id")
    private Integer chatId;

    @Id
    @Column(name = "knowledge_base_id")
    private Integer knowledgeBaseId;
}