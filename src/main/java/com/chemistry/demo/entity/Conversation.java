package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String modelUsed;

    // @OrderBy bắt buộc: không có thì Hibernate trả tin nhắn theo thứ tự tuỳ ý
    // của DB — màn lịch sử chat từng hiển thị câu hỏi nằm DƯỚI câu trả lời.
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<ConversationMessage> messages = new ArrayList<>();
}
