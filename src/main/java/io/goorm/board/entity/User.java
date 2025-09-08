package io.goorm.board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity  // JPA 엔티티임을 나타냄
@Table(name = "users")  // 실제 테이블명 지정
@Getter
@Setter  // Lombok: getter/setter 자동 생성
@NoArgsConstructor  // Lombok: 기본 생성자 자동 생성
@AllArgsConstructor  // Lombok: 모든 필드 생성자 자동 생성
@ToString  // Lombok: toString() 메서드 자동 생성
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "{validation.email.invalid}")
    @Column(nullable = false, unique = true)
    @NotBlank(message = "{validation.email.required}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 4, message = "{validation.password.size}")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "{validation.nickname.required}")
    @Size(min = 2, max = 20, message = "{validation.nickname.size}")
    @Column(nullable = false)
    private String nickname;

    @Column(name= "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name= "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
