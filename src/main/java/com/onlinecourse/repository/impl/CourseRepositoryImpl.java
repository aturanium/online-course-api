package com.onlinecourse.repository.impl;

import com.onlinecourse.pojo.Course;
import com.onlinecourse.pojo.CourseStatus;
import com.onlinecourse.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CourseRepositoryImpl implements CourseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Course course) {
        if (course.getId() == null) {
            entityManager.persist(course);
        } else {
            entityManager.merge(course);
        }
    }

    @Override
    public void delete(Course course) {
        entityManager.remove(entityManager.contains(course) ? course : entityManager.merge(course));
    }

    @Override
    public Course findById(Integer id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public List<Course> findAll() {
        return entityManager.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    @Override
    public List<Course> findByTeacherId(Integer teacherId) {
        return entityManager.createQuery("SELECT c FROM Course c WHERE c.teacher.id = :teacherId", Course.class)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Course> searchCourses(String keyword, Integer teacherId, CourseStatus status, String sortByPrice, String sortByName, Integer page) {
        StringBuilder hql = new StringBuilder("SELECT c FROM Course c WHERE 1=1 ");

        if (status != null) {
            hql.append("AND c.status = :status ");
        }
        if (keyword != null && !keyword.isEmpty()) {
            hql.append("AND (c.name LIKE :keyword OR c.teacher.firstName LIKE :keyword OR c.teacher.lastName LIKE :keyword) ");
        }
        if (teacherId != null) {
            hql.append("AND c.teacher.id = :teacherId ");
        }

        if (sortByPrice != null && !sortByPrice.isEmpty()) {
            hql.append("ORDER BY c.price ").append(sortByPrice.equalsIgnoreCase("ASC") ? "ASC" : "DESC");
        } else if (sortByName != null && !sortByName.isEmpty()) {
            hql.append("ORDER BY c.name ").append(sortByName.equalsIgnoreCase("ASC") ? "ASC" : "DESC");
        } else {
            hql.append("ORDER BY c.createdAt DESC");
        }

        TypedQuery<Course> query = entityManager.createQuery(hql.toString(), Course.class);

        if (status != null) {
            query.setParameter("status", status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (teacherId != null) {
            query.setParameter("teacherId", teacherId);
        }

        int pageSize = 20;
        int currentPage = (page != null && page > 0) ? page : 1;
        int offset = (currentPage - 1) * pageSize;

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public Long countActiveCourses() {
        return entityManager.createQuery(
                "SELECT COUNT(c) FROM Course c WHERE c.status = 'ACTIVE'", Long.class)
                .getSingleResult();
    }
}
