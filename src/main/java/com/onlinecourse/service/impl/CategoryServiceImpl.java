package com.onlinecourse.service.impl;

import com.onlinecourse.dto.request.CategoryRequest;
import com.onlinecourse.dto.response.CategoryResponse;
import com.onlinecourse.pojo.Category;
import com.onlinecourse.repository.CategoryRepository;
import com.onlinecourse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(cat -> {
            CategoryResponse res = new CategoryResponse();
            res.setId(cat.getId());
            res.setName(cat.getName());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new RuntimeException("Tên chủ đề đã tồn tại!");
        }
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy chủ đề!");
        }

        Category existing = categoryRepository.findByName(request.getName());
        if (existing != null && !existing.getId().equals(id)) {
            throw new RuntimeException("Tên chủ đề đã tồn tại!");
        }

        category.setName(request.getName());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy chủ đề!");
        }
        categoryRepository.delete(category);
    }
}
