package com.onlinecourse.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.onlinecourse.dto.request.*;
import com.onlinecourse.dto.response.*;
import com.onlinecourse.pojo.*;
import com.onlinecourse.repository.*;
import com.onlinecourse.service.CourseService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private Cloudinary cloudinary;

    private String uploadMedia(MultipartFile file, String resourceType) {
        if (file != null && !file.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
                return res.get("secure_url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload file lên Cloudinary!");
            }
        }
        return null;
    }

    private Course getCourseAndCheckTeacher(Integer courseId, String teacherEmail) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new RuntimeException("Không tìm thấy khóa học!");
        }
        if (!course.getTeacher().getEmail().equals(teacherEmail)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên khóa học này!");
        }
        return course;
    }

    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse res = new CourseResponse();
        res.setId(course.getId());
        res.setImage(course.getImage());
        res.setName(course.getName());
        res.setTeacherName(course.getTeacher().getLastName() + " " + course.getTeacher().getFirstName());
        res.setPrice(course.getPrice());
        res.setDuration(course.getDuration());
        res.setStatus(course.getStatus());
        res.setDescription(course.getDescription());
        res.setCategoryId(course.getCategory() != null ? course.getCategory().getId() : null);
        return res;
    }

    @Override
    public void createCourse(String teacherEmail, CourseRequest request) {
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher.getIsVerified() == null || !teacher.getIsVerified()) {
            throw new RuntimeException("Tài khoản của bạn chưa được Admin xác minh! Vui lòng chờ phê duyệt trước khi tạo khóa học.");
        }
        Category category = categoryRepository.findById(request.getCategoryId());
        if (category == null) {
            throw new RuntimeException("Chủ đề không tồn tại!");
        }

        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setDuration(request.getDuration());
        course.setTeacher(teacher);
        course.setCategory(category);
        course.setStatus(CourseStatus.PENDING);

        course.setImage(uploadMedia(request.getImage(), "image"));
        course.setVideoIntro(uploadMedia(request.getVideoIntro(), "video"));

        courseRepository.save(course);
    }

    @Override
    public void updateCourse(String teacherEmail, Integer courseId, CourseRequest request) {
        Course course = getCourseAndCheckTeacher(courseId, teacherEmail);

        if (request.getName() != null) {
            course.setName(request.getName());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getDuration() != null) {
            course.setDuration(request.getDuration());
        }

        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId());
            if (cat != null) {
                course.setCategory(cat);
            }
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            course.setImage(uploadMedia(request.getImage(), "image"));
        }
        if (request.getVideoIntro() != null && !request.getVideoIntro().isEmpty()) {
            course.setVideoIntro(uploadMedia(request.getVideoIntro(), "video"));
        }

        course.setStatus(CourseStatus.PENDING);
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Integer courseId, String email, String role) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new RuntimeException("Không tìm thấy khóa học!");
        }
        if (!"ADMIN".equals(role)) {
            if (!course.getTeacher().getEmail().equals(email)) {
                throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền xóa khóa học của giảng viên khác!");
            }
        }
        courseRepository.delete(course);
    }

    @Override
    public void approveCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new RuntimeException("Không tìm thấy khóa học!");
        }
        course.setStatus(CourseStatus.ACTIVE);
        courseRepository.save(course);
    }

    @Override
    public List<CourseResponse> getAllCoursesForAdmin() {
        return courseRepository.findAll().stream().map(this::mapToCourseResponse).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getCoursesForTeacher(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        return courseRepository.findByTeacherId(teacher.getId()).stream()
                .map(this::mapToCourseResponse).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> searchActiveCourses(String keyword, Integer teacherId, String sortByPrice, String sortByName, Integer page) {
        return courseRepository.searchCourses(keyword, teacherId, CourseStatus.ACTIVE, sortByPrice, sortByName, page)
                .stream().map(this::mapToCourseResponse).collect(Collectors.toList());
    }

    @Override
    public CourseDetailResponse getCourseDetail(Integer courseId, String currentUserEmail, String role) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new RuntimeException("Không tìm thấy khóa học!");
        }

        CourseDetailResponse res = new CourseDetailResponse();
        res.setVideoIntro(course.getVideoIntro());
        res.setName(course.getName());
        res.setTeacherName(course.getTeacher().getLastName() + " " + course.getTeacher().getFirstName());
        res.setCategoryName(course.getCategory() != null ? course.getCategory().getName() : "");
        res.setPrice(course.getPrice());
        res.setDuration(course.getDuration());
        res.setStatus(course.getStatus());
        res.setDescription(course.getDescription());

        boolean canViewLessons = false;
        boolean isJoined = false;
        if ("ADMIN".equals(role)) {
            canViewLessons = true;
        } else if ("TEACHER".equals(role) && course.getTeacher().getEmail().equals(currentUserEmail)) {
            canViewLessons = true;
        } else if (currentUserEmail != null) {
            User student = userRepository.findByEmail(currentUserEmail);
            if (student != null) {
                Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, student.getId());
                if (enrollment != null) {
                    canViewLessons = true;
                    isJoined = true;
                }
            }
        }

        res.setIsJoined(isJoined);

        if (canViewLessons) {
            List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
            res.setLessons(lessons.stream().map(l -> {
                LessonResponse lr = new LessonResponse();
                lr.setId(l.getId());
                lr.setTitle(l.getTitle());
                lr.setContentUrl(l.getContentUrl());
                return lr;
            }).collect(Collectors.toList()));
        } else {
            res.setLessons(new ArrayList<>());
        }

        return res;
    }

    @Override
    public List<MyCourseResponse> getMyCourses(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        return enrollments.stream().map(e -> {
            Course course = e.getCourse();
            MyCourseResponse res = new MyCourseResponse();
            res.setId(course.getId());
            res.setImage(course.getImage());
            res.setName(course.getName());
            res.setTeacherName(course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName());
            res.setDuration(course.getDuration());
            res.setPrice(course.getPrice());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void addLesson(String teacherEmail, Integer courseId, LessonRequest request) {
        Course course = getCourseAndCheckTeacher(courseId, teacherEmail);
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(request.getTitle());
        lesson.setContentUrl(request.getContentUrl());
        Integer currentMaxIndex = lessonRepository.findMaxOrderIndexByCourseId(courseId);
        lesson.setOrderIndex(currentMaxIndex != null ? currentMaxIndex + 1 : 1);
        lessonRepository.save(lesson);
        if (course.getEnrollments() != null) {
            for (Enrollment enrollment : course.getEnrollments()) {
                if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
                    enrollment.setStatus(EnrollmentStatus.LEARNING);
                    enrollmentRepository.save(enrollment);
                }
            }
        }
    }

    @Override
    public void updateLesson(String teacherEmail, Integer lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId);
        if (lesson == null) {
            throw new RuntimeException("Không tìm thấy bài học!");
        }
        getCourseAndCheckTeacher(lesson.getCourse().getId(), teacherEmail);

        if (request.getTitle() != null) {
            lesson.setTitle(request.getTitle());
        }
        if (request.getContentUrl() != null) {
            lesson.setContentUrl(request.getContentUrl());
        }
        lessonRepository.save(lesson);
    }

    @Override
    public void deleteLesson(String teacherEmail, Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId);
        if (lesson == null) {
            throw new RuntimeException("Không tìm thấy bài học!");
        }
        getCourseAndCheckTeacher(lesson.getCourse().getId(), teacherEmail);
        lessonRepository.delete(lesson);
    }

    @Override
    public void markLessonAsCompleted(String studentEmail, Integer lessonId) {
        User student = userRepository.findByEmail(studentEmail);
        Lesson lesson = lessonRepository.findById(lessonId);
        if (lesson == null) {
            throw new RuntimeException("Không tìm thấy bài học!");
        }

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(lesson.getCourse().getId(), student.getId());
        if (enrollment == null) {
            throw new RuntimeException("Bạn chưa tham gia khóa học này!");
        }

        Progress progress = progressRepository.findByStudentAndLesson(student.getId(), lessonId);
        if (progress == null) {
            progress = new Progress();
            progress.setStudent(student);
            progress.setLesson(lesson);
        }

        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        Long completed = enrollmentRepository.countCompletedLessons(student.getId(), lesson.getCourse().getId());
        int totalLessons = lessonRepository.findByCourseId(lesson.getCourse().getId()).size();

        if (totalLessons > 0 && completed.intValue() == totalLessons) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollmentRepository.save(enrollment);
        }
    }

    @Override
    public void enrollStudentByEmail(String teacherEmail, Integer courseId, EnrollStudentRequest request) {
        Course course = getCourseAndCheckTeacher(courseId, teacherEmail);
        User student = userRepository.findByEmail(request.getEmail());
        if (student == null || student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Không tìm thấy sinh viên với email này!");
        }

        Enrollment existing = enrollmentRepository.findByCourseIdAndStudentId(courseId, student.getId());
        if (existing != null) {
            throw new RuntimeException("Sinh viên này đã có trong khóa học!");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.LEARNING);
        enrollmentRepository.save(enrollment);

        if (course.getPrice() != null && course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = new Transaction();
            transaction.setStudent(student);

            BigDecimal coursePrice = new BigDecimal(course.getPrice().toString());
            transaction.setAmount(coursePrice);

            transaction.setPaymentMethod(PaymentMethod.CASH);
            transaction.setStatus(TransactionStatus.SUCCESS);

            TransactionItem item = new TransactionItem();
            item.setTransaction(transaction);
            item.setCourse(course);
            item.setPrice(coursePrice);
            List<TransactionItem> items = new ArrayList<>();
            items.add(item);
            transaction.setTransactionItems(items);

            transactionRepository.save(transaction);
        }
    }

    @Override
    public void removeStudentFromCourse(String teacherEmail, Integer courseId, Integer studentId) {
        getCourseAndCheckTeacher(courseId, teacherEmail);
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);
        if (enrollment == null) {
            throw new RuntimeException("Sinh viên không thuộc khóa học này!");
        }
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public List<StudentInCourseResponse> getStudentsInCourse(String teacherEmail, Integer courseId) {
        getCourseAndCheckTeacher(courseId, teacherEmail);

        List<Enrollment> enrollments = courseRepository.findById(courseId).getEnrollments();
        int totalLessons = lessonRepository.findByCourseId(courseId).size();

        if (enrollments == null) {
            enrollments = new ArrayList<>();
        }

        return enrollments.stream().map(e -> {
            StudentInCourseResponse res = new StudentInCourseResponse();
            res.setId(e.getStudent().getId());
            res.setAvatar(e.getStudent().getAvatar());
            res.setFirstName(e.getStudent().getFirstName());
            res.setLastName(e.getStudent().getLastName());
            res.setEmail(e.getStudent().getEmail());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            res.setEnrolledAt(e.getEnrolledAt().format(formatter));
            res.setStatus(e.getStatus());

            Long completed = enrollmentRepository.countCompletedLessons(e.getStudent().getId(), courseId);
            int percent = totalLessons == 0 ? 0 : (int) ((completed * 100) / totalLessons);
            res.setProgress(percent);

            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public StudentProgressResponse getStudentProgress(String teacherEmail, Integer courseId, Integer studentId) {
        getCourseAndCheckTeacher(courseId, teacherEmail);
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId);
        if (enrollment == null) {
            throw new RuntimeException("Sinh viên không thuộc khóa học này!");
        }

        int totalLessons = lessonRepository.findByCourseId(courseId).size();
        Long completed = enrollmentRepository.countCompletedLessons(studentId, courseId);
        int percent = totalLessons == 0 ? 0 : (int) ((completed * 100) / totalLessons);

        StudentProgressResponse res = new StudentProgressResponse();
        res.setAvatar(enrollment.getStudent().getAvatar());
        res.setFirstName(enrollment.getStudent().getFirstName());
        res.setLastName(enrollment.getStudent().getLastName());
        res.setEmail(enrollment.getStudent().getEmail());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        res.setEnrolledAt(enrollment.getEnrolledAt().format(formatter));
        res.setCompletedLessons(completed.intValue());
        res.setTotalLessons(totalLessons);
        res.setProgressPercentage(percent);
        res.setStatus(enrollment.getStatus());
        return res;
    }

    @Override
    public CourseStatisticResponse getCourseStatistic(String teacherEmail, Integer courseId) {
        Course course = getCourseAndCheckTeacher(courseId, teacherEmail);

        List<Enrollment> enrollments = course.getEnrollments();
        int studentCount = (enrollments != null) ? enrollments.size() : 0;

        BigDecimal totalRev = course.getPrice() != null
                ? course.getPrice().multiply(new BigDecimal(studentCount))
                : BigDecimal.ZERO;

        CourseStatisticResponse res = new CourseStatisticResponse();
        res.setTotalStudents(studentCount);
        res.setTotalRevenue(totalRev);

        return res;
    }
}
