package com.app.domain.item.repositories;

import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.member.entities.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT i FROM Item i WHERE i.id = :itemId")
    Optional<Item> findByIdWithLock(@Param("itemId") UUID itemId);

    @Query("SELECT i FROM Item i WHERE i.category IN (:categories)")
    Page<Item> findByCategories(@Param("categories") Set<Category> categories, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.title LIKE %:title%")
    Page<Item> findByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.seller = :seller")
    Page<Item> findBySeller(@Param("seller") Member seller, Pageable pageable);
}
