package com.app.domain.member.repositories;

import com.app.domain.member.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.username LIKE :username")
    Optional<Member> findByUsername(@Param("username") String username);
}
