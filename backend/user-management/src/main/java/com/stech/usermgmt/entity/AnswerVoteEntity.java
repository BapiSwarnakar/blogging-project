package com.stech.usermgmt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_answer_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"answer_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private AnswerEntity answer;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer voteType; // 1 for UP, -1 for DOWN

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
