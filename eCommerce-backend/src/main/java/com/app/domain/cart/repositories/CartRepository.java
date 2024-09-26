package com.app.domain.cart.repositories;

import com.app.domain.cart.entities.Cart;
import com.app.domain.member.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT c FROM Cart c WHERE c.owner = :owner")
    Optional<Cart> findByOwner(@Param("owner") Member owner);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.owner = :owner")
    void deleteAllByOwner(@Param("owner") Member owner);
}