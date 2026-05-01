package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.User;
import com.onlinecourse.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    @Override
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    @Override
    public User findById(Integer id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByEmail(String email) {
        try {
            return entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery(
                "SELECT u FROM User u", User.class)
                .getResultList();
    }

    @Override
    public List<User> searchByEmailContaining(String email) {
        return entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email LIKE :email", User.class)
                .setParameter("email", "%" + email + "%")
                .getResultList();
    }

    @Override
    public Long countStudents() {
        return entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.role = 'STUDENT'", Long.class)
                .getSingleResult();
    }
}
