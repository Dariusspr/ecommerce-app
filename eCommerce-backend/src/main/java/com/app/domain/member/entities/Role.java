package com.app.domain.member.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @NotNull
    @Column(name = "role_title", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Role.RoleTitle title;

    public Role() {
    }

    public Role(RoleTitle role) {
        title = role;
    }

    // AUTO GENERATED

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleTitle getTitle() {
        return title;
    }

    public void setTitle(RoleTitle title) {
        this.title = title;
    }

    public enum RoleTitle {
        MEMBER,
        ADMIN,
    }
}
