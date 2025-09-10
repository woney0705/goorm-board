package io.goorm.board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity  // JPA 엔티티임을 나타냄
@Table(name = "users")  // 실제 테이블명 지정
@Getter
@Setter  // Lombok: getter/setter 자동 생성
@NoArgsConstructor  // Lombok: 기본 생성자 자동 생성
public class User implements UserDetails {

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

    // UserDetails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
