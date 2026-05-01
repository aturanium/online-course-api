package com.onlinecourse.repository;

import com.onlinecourse.pojo.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> findByStudentId(Integer studentId);

    Transaction findById(Integer id);

    public BigDecimal calculateTotalRevenue();

    BigDecimal calculateRevenueByCourse(Integer courseId);
}
