package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Enrollment;
import com.onlinecourse.repository.EnrollmentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            entityManager.persist(enrollment);
        } else {
            entityManager.merge(enrollment);
        }
    }

    @Override
    public void delete(Enrollment enrollment) {
        entityManager.remove(entityManager.contains(enrollment) ? enrollment : entityManager.merge(enrollment));
    }

    @Override
    public Enrollment findByCourseIdAndStudentId(Integer courseId, Integer studentId) {
        try {
            return entityManager.createQuery(
                    "SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.student.id = :studentId", Enrollment.class)
                    .setParameter("courseId", courseId)
                    .setParameter("studentId", studentId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Enrollment> findByStudentId(Integer studentId) {
        return entityManager.createQuery("SELECT e FROM Enrollment e WHERE e.student.id = :studentId", Enrollment.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public Long countCompletedLessons(Integer studentId, Integer courseId) {
        return entityManager.createQuery(
                "SELECT COUNT(p) FROM Progress p WHERE p.student.id = :studentId AND p.lesson.course.id = :courseId AND p.isCompleted = true", Long.class)
                .setParameter("studentId", studentId)
                .setParameter("courseId", courseId)
                .getSingleResult();
    }
}
