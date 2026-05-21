package com.viniciusDizatnikis.auth_service.domain.token;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_email_tokens")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PendingEmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;        // ainda nao e um usuario
    private String codeHash;
    private Integer attempts;
    private Boolean used;
    private Boolean verified;    // true = passou pela verificacao
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.attempts = 0;
        this.used = false;
        this.verified = false;
    }
}