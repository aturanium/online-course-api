package com.onlinecourse.repository;

import com.onlinecourse.pojo.Progress;

public interface ProgressRepository {

    void save(Progress progress);

    Progress findByStudentAndLesson(Integer studentId, Integer lessonId);
}
