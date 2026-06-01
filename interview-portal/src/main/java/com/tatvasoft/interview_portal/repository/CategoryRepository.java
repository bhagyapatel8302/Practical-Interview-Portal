package com.tatvasoft.interview_portal.repository;

import com.tatvasoft.interview_portal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
