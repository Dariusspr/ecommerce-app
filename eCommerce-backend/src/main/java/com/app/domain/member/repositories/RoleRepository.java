package com.app.domain.member.repositories;

import com.app.domain.member.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.title = :title")
    Optional<Role> findByTitle(@Param("title") Role.RoleTitle role);
}
