package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Transaction;
import com.onlinecourse.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class TransactionRepositoryImpl implements TransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Transaction transaction) {
        if (transaction.getId() == null) {
            entityManager.persist(transaction);
        } else {
            entityManager.merge(transaction);
        }
    }

    @Override
    public List<Transaction> findByStudentId(Integer studentId) {
        return entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.student.id = :studentId ORDER BY t.createdAt DESC", Transaction.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public Transaction findById(Integer id) {
        return entityManager.find(Transaction.class, id);
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        BigDecimal total = entityManager.createQuery(
                "SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'SUCCESS'", BigDecimal.class)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateRevenueByCourse(Integer courseId) {
        try {
            BigDecimal total = entityManager.createQuery(
                    "SELECT SUM(ti.price) FROM TransactionItem ti WHERE ti.course.id = :courseId AND ti.transaction.status = 'SUCCESS'", BigDecimal.class)
                    .setParameter("courseId", courseId)
                    .getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
