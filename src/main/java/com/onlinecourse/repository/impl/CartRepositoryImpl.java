package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Cart;
import com.onlinecourse.repository.CartRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class CartRepositoryImpl implements CartRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Cart cart) {
        if (cart.getId() == null) {
            entityManager.persist(cart);
        } else {
            entityManager.merge(cart);
        }
    }

    @Override
    public void delete(Cart cart) {
        entityManager.remove(entityManager.contains(cart) ? cart : entityManager.merge(cart));
    }

    @Override
    public void deleteByStudentId(Integer studentId) {
        entityManager.createQuery("DELETE FROM Cart c WHERE c.student.id = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public Cart findByStudentAndCourse(Integer studentId, Integer courseId) {
        try {
            return entityManager.createQuery(
                    "SELECT c FROM Cart c WHERE c.student.id = :studentId AND c.course.id = :courseId", Cart.class)
                    .setParameter("studentId", studentId)
                    .setParameter("courseId", courseId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Cart> findByStudentId(Integer studentId) {
        return entityManager.createQuery("SELECT c FROM Cart c WHERE c.student.id = :studentId ORDER BY c.addedAt DESC", Cart.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }
}
