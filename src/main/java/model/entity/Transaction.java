package model.entity;

import model.enums.PaymentMethod;
import model.enums.TransactionStatus;

import java.util.Date;
import java.util.List;

public class Transaction {
    private String idTransaction;
    private String userEmail;
    private Integer totalPriceAmount;
    private TransactionStatus status;
    private Date date;
    private PaymentMethod paymentMethod;
    private List<TransactionDetail> details;

    public Transaction() {
    }

    public Transaction(String idTransaction, String userEmail, Integer totalPriceAmount,
                       TransactionStatus status, Date date, PaymentMethod paymentMethod) {
        this.idTransaction = idTransaction;
        this.userEmail = userEmail;
        this.totalPriceAmount = totalPriceAmount;
        this.status = status;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getTotalPriceAmount() {
        return totalPriceAmount;
    }

    public void setTotalPriceAmount(Integer totalPriceAmount) {
        this.totalPriceAmount = totalPriceAmount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<TransactionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TransactionDetail> details) {
        this.details = details;
    }
}
