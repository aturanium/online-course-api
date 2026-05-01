package com.onlinecourse.dto.request;

import com.onlinecourse.pojo.PaymentMethod;

public class PaymentRequest {

    private PaymentMethod paymentMethod;

    public PaymentRequest() {
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
