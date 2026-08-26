package model.dto.request;

import model.enums.PaymentMethod;

public class TransactionRequest {
    private Integer productId;
    private Integer quantity;
    private String userEmail;
    private Integer paidAmount;
    private PaymentMethod paymentMethod;

    public TransactionRequest() {
    }

    public TransactionRequest(Integer productId, Integer quantity, String userEmail, Integer paidAmount, PaymentMethod paymentMethod) {
        this.productId = productId;
        this.quantity = quantity;
        this.userEmail = userEmail;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Integer paidAmount) {
        this.paidAmount = paidAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
