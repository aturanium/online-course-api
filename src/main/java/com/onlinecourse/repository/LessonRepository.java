package com.onlinecourse.repository;

import com.onlinecourse.pojo.Lesson;
import java.util.List;

public interface LessonRepository {

    void save(Lesson lesson);

    void delete(Lesson lesson);

    Lesson findById(Integer id);

    List<Lesson> findByCourseId(Integer courseId);

    Integer findMaxOrderIndexByCourseId(Integer courseId);
}
