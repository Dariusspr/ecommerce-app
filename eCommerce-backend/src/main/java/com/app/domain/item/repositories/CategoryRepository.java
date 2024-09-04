package com.app.domain.item.repositories;

import com.app.domain.item.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.title = :title ")
    Optional<Category> findByTitle(@Param("title") String title);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRoots();

    @Query("SELECT c FROM Category c WHERE c.parent = :parent")
    List<Category> findByParent(@Param("parent") Category parent);
}
