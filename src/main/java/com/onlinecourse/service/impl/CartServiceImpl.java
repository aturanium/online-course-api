package com.onlinecourse.service.impl;

import com.onlinecourse.dto.request.PaymentRequest;
import com.onlinecourse.dto.response.CartItemResponse;
import com.onlinecourse.dto.response.CartResponse;
import com.onlinecourse.dto.response.TransactionItemResponse;
import com.onlinecourse.dto.response.TransactionResponse;
import com.onlinecourse.pojo.*;
import com.onlinecourse.repository.*;
import com.onlinecourse.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public CartResponse getCart(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail);
        List<Cart> carts = cartRepository.findByStudentId(student.getId());

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Cart cart : carts) {
            Course course = cart.getCourse();
            CartItemResponse item = new CartItemResponse();
            item.setCourseId(course.getId());
            item.setImage(course.getImage());
            item.setCourseName(course.getName());
            item.setTeacherName(course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName());
            item.setPrice(course.getPrice());
            items.add(item);

            total = total.add(course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO);
        }

        CartResponse res = new CartResponse();
        res.setItems(items);
        res.setTotalPrice(total);
        return res;
    }

    @Override
    public void addToCart(String studentEmail, Integer courseId) {
        User student = userRepository.findByEmail(studentEmail);
        Course course = courseRepository.findById(courseId);

        if (course == null || course.getStatus() != CourseStatus.ACTIVE) {
            throw new RuntimeException("Khóa học không tồn tại hoặc chưa được duyệt!");
        }

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, student.getId());
        if (enrollment != null) {
            throw new RuntimeException("Bạn đã sở hữu khóa học này rồi!");
        }

        Cart existingCart = cartRepository.findByStudentAndCourse(student.getId(), courseId);
        if (existingCart != null) {
            throw new RuntimeException("Khóa học đã có trong giỏ hàng!");
        }

        Cart cart = new Cart();
        cart.setStudent(student);
        cart.setCourse(course);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(String studentEmail, Integer courseId) {
        User student = userRepository.findByEmail(studentEmail);
        Cart cart = cartRepository.findByStudentAndCourse(student.getId(), courseId);
        if (cart != null) {
            cartRepository.delete(cart);
        }
    }

    @Override
    public void processPayment(String studentEmail, PaymentRequest request) {
        User student = userRepository.findByEmail(studentEmail);
        List<Cart> carts = cartRepository.findByStudentId(student.getId());

        if (carts.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống, không thể thanh toán!");
        }

        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setGatewayTransactionId("TXN-" + System.currentTimeMillis());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransactionItem> transactionItems = new ArrayList<>();

        for (Cart cart : carts) {
            Course course = cart.getCourse();

            if (course.getStatus() != CourseStatus.ACTIVE) {
                throw new RuntimeException("Khóa học '" + course.getName() + "' hiện không khả dụng!");
            }

            BigDecimal price = course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price);

            TransactionItem item = new TransactionItem();
            item.setTransaction(transaction);
            item.setCourse(course);
            item.setPrice(price);
            transactionItems.add(item);

            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudent(student);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setStatus(EnrollmentStatus.LEARNING);
            enrollmentRepository.save(enrollment);
        }

        transaction.setAmount(totalAmount);
        transaction.setTransactionItems(transactionItems);

        transactionRepository.save(transaction);

        cartRepository.deleteByStudentId(student.getId());
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail);
        List<Transaction> transactions = transactionRepository.findByStudentId(student.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return transactions.stream().map(t -> {
            TransactionResponse res = new TransactionResponse();
            res.setTotalAmount(t.getAmount());
            res.setPaymentMethod(t.getPaymentMethod());
            res.setStatus(t.getStatus());
            res.setPaymentDate(t.getCreatedAt().format(formatter));

            List<TransactionItemResponse> itemResponses = t.getTransactionItems().stream().map(ti -> {
                TransactionItemResponse ir = new TransactionItemResponse();
                ir.setImage(ti.getCourse().getImage());
                ir.setCourseName(ti.getCourse().getName());
                ir.setTeacherName(ti.getCourse().getTeacher().getFirstName() + " " + ti.getCourse().getTeacher().getLastName());
                ir.setPrice(ti.getPrice());
                return ir;
            }).collect(Collectors.toList());

            res.setItems(itemResponses);
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer createPayment(String studentEmail, PaymentRequest request) {
        User student = userRepository.findByEmail(studentEmail);
        List<Cart> carts = cartRepository.findByStudentId(student.getId());

        if (carts.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống!");
        }

        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setGatewayTransactionId("TXN-" + System.currentTimeMillis());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransactionItem> transactionItems = new ArrayList<>();

        for (Cart cart : carts) {
            Course course = cart.getCourse();
            if (course.getStatus() != CourseStatus.ACTIVE) {
                throw new RuntimeException("Khóa học '" + course.getName() + "' hiện không khả dụng!");
            }
            BigDecimal price = course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price);

            TransactionItem item = new TransactionItem();
            item.setTransaction(transaction);
            item.setCourse(course);
            item.setPrice(price);
            transactionItems.add(item);
        }

        transaction.setAmount(totalAmount);
        transaction.setTransactionItems(transactionItems);

        transactionRepository.save(transaction);

        return transaction.getId();
    }

    @Override
    public void updatePaymentStatus(String studentEmail, Integer transactionId, String status) {
        User student = userRepository.findByEmail(studentEmail);
        Transaction transaction = transactionRepository.findById(transactionId);

        if (transaction == null || !transaction.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Giao dịch không tồn tại hoặc không hợp lệ!");
        }
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Giao dịch này đã được xử lý rồi!");
        }

        if ("SUCCESS".equals(status)) {
            transaction.setStatus(TransactionStatus.SUCCESS);

            for (TransactionItem item : transaction.getTransactionItems()) {
                Course course = item.getCourse();

                Enrollment existing = enrollmentRepository.findByCourseIdAndStudentId(course.getId(), student.getId());
                if (existing == null) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setCourse(course);
                    enrollment.setStudent(student);
                    enrollment.setEnrolledAt(LocalDateTime.now());
                    enrollment.setStatus(EnrollmentStatus.LEARNING);
                    enrollmentRepository.save(enrollment);
                }
            }

            cartRepository.deleteByStudentId(student.getId());

        } else if ("FAILED".equals(status)) {
            transaction.setStatus(TransactionStatus.FAILED);

        } else {
            throw new RuntimeException("Trạng thái không hợp lệ!");
        }

        transactionRepository.save(transaction);
    }
}
