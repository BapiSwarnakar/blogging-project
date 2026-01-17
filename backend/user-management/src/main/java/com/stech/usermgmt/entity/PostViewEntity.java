package com.stech.usermgmt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_post_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(nullable = true)
    private Long userId; // Null for anonymous views

    @Column(nullable = false)
    private String ipAddress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;
}
