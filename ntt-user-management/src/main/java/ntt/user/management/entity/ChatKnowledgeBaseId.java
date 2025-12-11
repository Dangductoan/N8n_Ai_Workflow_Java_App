package ntt.user.management.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ChatKnowledgeBaseId implements Serializable {
    private Integer chatId;
    private Integer knowledgeBaseId;

    public ChatKnowledgeBaseId() {}

    public ChatKnowledgeBaseId(Integer chatId, Integer knowledgeBaseId) {
        this.chatId = chatId;
        this.knowledgeBaseId = knowledgeBaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatKnowledgeBaseId that = (ChatKnowledgeBaseId) o;
        return Objects.equals(chatId, that.chatId) &&
                Objects.equals(knowledgeBaseId, that.knowledgeBaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, knowledgeBaseId);
    }
}
