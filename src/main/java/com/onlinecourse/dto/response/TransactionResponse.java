package com.onlinecourse.dto.response;

import com.onlinecourse.pojo.PaymentMethod;
import com.onlinecourse.pojo.TransactionStatus;
import java.math.BigDecimal;
import java.util.List;

public class TransactionResponse {

    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private TransactionStatus status;
    private String paymentDate;
    private List<TransactionItemResponse> items;

    public TransactionResponse() {
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public List<TransactionItemResponse> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemResponse> items) {
        this.items = items;
    }
}
