package com.onlinecourse.repository;

import com.onlinecourse.pojo.Category;
import java.util.List;

public interface CategoryRepository {

    void save(Category category);

    void delete(Category category);

    Category findById(Integer id);

    Category findByName(String name);

    List<Category> findAll();
}
