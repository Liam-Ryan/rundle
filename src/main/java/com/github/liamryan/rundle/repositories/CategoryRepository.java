package com.github.liamryan.rundle.repositories;

import com.github.liamryan.rundle.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}