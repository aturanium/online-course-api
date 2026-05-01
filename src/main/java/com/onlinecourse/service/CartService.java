package com.onlinecourse.service;

import com.onlinecourse.dto.request.PaymentRequest;
import com.onlinecourse.dto.response.CartResponse;
import com.onlinecourse.dto.response.TransactionResponse;
import java.util.List;

public interface CartService {

    CartResponse getCart(String studentEmail);

    void addToCart(String studentEmail, Integer courseId);

    void removeFromCart(String studentEmail, Integer courseId);

    void processPayment(String studentEmail, PaymentRequest request);

    List<TransactionResponse> getTransactionHistory(String studentEmail);

    Integer createPayment(String studentEmail, PaymentRequest request);

    void updatePaymentStatus(String studentEmail, Integer transactionId, String status);
}
