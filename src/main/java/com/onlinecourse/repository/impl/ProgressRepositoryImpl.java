package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Progress;
import com.onlinecourse.repository.ProgressRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ProgressRepositoryImpl implements ProgressRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Progress progress) {
        if (progress.getId() == null) {
            entityManager.persist(progress);
        } else {
            entityManager.merge(progress);
        }
    }

    @Override
    public Progress findByStudentAndLesson(Integer studentId, Integer lessonId) {
        try {
            return entityManager.createQuery(
                    "SELECT p FROM Progress p WHERE p.student.id = :studentId AND p.lesson.id = :lessonId", Progress.class)
                    .setParameter("studentId", studentId)
                    .setParameter("lessonId", lessonId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
