package com.onlinecourse.repository;

import com.onlinecourse.pojo.Cart;
import java.util.List;

public interface CartRepository {

    void save(Cart cart);

    void delete(Cart cart);

    void deleteByStudentId(Integer studentId);

    Cart findByStudentAndCourse(Integer studentId, Integer courseId);

    List<Cart> findByStudentId(Integer studentId);
}
