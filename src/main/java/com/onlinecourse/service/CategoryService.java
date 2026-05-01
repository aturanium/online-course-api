package com.onlinecourse.service;

import com.onlinecourse.dto.request.CategoryRequest;
import com.onlinecourse.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    void createCategory(CategoryRequest request);

    void updateCategory(Integer id, CategoryRequest request);

    void deleteCategory(Integer id);
}
