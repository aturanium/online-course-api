package com.onlinecourse.repository;

import com.onlinecourse.pojo.User;
import java.util.List;

public interface UserRepository {

    void save(User user);

    void delete(User user);

    User findById(Integer id);

    User findByEmail(String email);

    List<User> findAll();

    List<User> searchByEmailContaining(String email);

    public Long countStudents();
}
