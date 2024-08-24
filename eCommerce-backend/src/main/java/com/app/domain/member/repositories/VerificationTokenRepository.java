package com.app.domain.member.repositories;

import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query("SELECT t FROM VerificationToken t WHERE t.code = :token")
    Optional<VerificationToken> findByCode(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.member = :member")
    void deleteByMember(@Param("member") Member member);

    @Query("SELECT t FROM VerificationToken t WHERE t.member = :member")
    Optional<VerificationToken> findByMember(@Param("member") Member member);
}
