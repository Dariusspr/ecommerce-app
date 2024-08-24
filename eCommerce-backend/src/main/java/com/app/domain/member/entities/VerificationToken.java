package com.app.domain.member.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @NotBlank
    @Column(name = "token", nullable = false, unique = true)
    private String code;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "token_expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    public VerificationToken() {
    }

    public VerificationToken(String code, Member member, LocalDateTime expiresAt) {
        this.code = code;
        this.member = member;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return getExpiresAt().isBefore(LocalDateTime.now());
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String token) {
        this.code = token;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
