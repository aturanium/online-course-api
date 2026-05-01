package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Lesson;
import com.onlinecourse.repository.LessonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class LessonRepositoryImpl implements LessonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Lesson lesson) {
        if (lesson.getId() == null) {
            entityManager.persist(lesson);
        } else {
            entityManager.merge(lesson);
        }
    }

    @Override
    public void delete(Lesson lesson) {
        entityManager.remove(entityManager.contains(lesson) ? lesson : entityManager.merge(lesson));
    }

    @Override
    public Lesson findById(Integer id) {
        return entityManager.find(Lesson.class, id);
    }

    @Override
    public List<Lesson> findByCourseId(Integer courseId) {
        return entityManager.createQuery("SELECT l FROM Lesson l WHERE l.course.id = :courseId ORDER BY l.orderIndex ASC", Lesson.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    @Override
    public Integer findMaxOrderIndexByCourseId(Integer courseId) {
        try {
            Integer maxIndex = entityManager.createQuery(
                    "SELECT MAX(l.orderIndex) FROM Lesson l WHERE l.course.id = :courseId", Integer.class)
                    .setParameter("courseId", courseId)
                    .getSingleResult();

            return maxIndex != null ? maxIndex : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
