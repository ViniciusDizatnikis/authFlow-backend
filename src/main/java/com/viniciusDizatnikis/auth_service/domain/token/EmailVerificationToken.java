package com.viniciusDizatnikis.auth_service.domain.token;

import com.viniciusDizatnikis.auth_service.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String codeHash;
    private Integer attempts;
    private Boolean used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.attempts = 0;
        this.used = false;
    }
}