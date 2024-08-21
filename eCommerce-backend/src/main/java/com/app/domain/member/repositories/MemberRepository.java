package com.app.domain.member.repositories;

import com.app.domain.member.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.username LIKE %:username%")
    Page<Member> findAllByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.username = :username")
    Optional<Member> findByUsername(String username);

}
