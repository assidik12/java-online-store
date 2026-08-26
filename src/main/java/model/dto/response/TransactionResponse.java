package model.dto.response;

import model.enums.PaymentMethod;
import model.enums.TransactionStatus;

import java.util.Date;
import java.util.List;

public class TransactionResponse {
    private String transactionId;
    private String userEmail;
    private Integer totalPriceAmount;
    private String formattedTotalPriceAmount;
    private Integer paidAmount;
    private Integer changeAmount;
    private TransactionStatus status;
    private Date date;
    private PaymentMethod paymentMethod;
    private List<TransactionDetailResponse> details;

    public TransactionResponse() {
    }

    public TransactionResponse(String transactionId, String userEmail, Integer totalPriceAmount, Integer paidAmount,
                               Integer changeAmount, TransactionStatus status, Date date, PaymentMethod paymentMethod,
                               List<TransactionDetailResponse> details) {
        this.transactionId = transactionId;
        this.userEmail = userEmail;
        this.totalPriceAmount = totalPriceAmount;
        this.formattedTotalPriceAmount = totalPriceAmount != null ? String.format("%,d", totalPriceAmount).replace(',', '.') : "0";
        this.paidAmount = paidAmount;
        this.changeAmount = changeAmount;
        this.status = status;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.details = details;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
        this.formattedTotalPriceAmount = totalPriceAmount != null ? String.format("%,d", totalPriceAmount).replace(',', '.') : "0";
    }

    public String getFormattedTotalPriceAmount() {
        return formattedTotalPriceAmount;
    }

    public void setFormattedTotalPriceAmount(String formattedTotalPriceAmount) {
        this.formattedTotalPriceAmount = formattedTotalPriceAmount;
    }

    public Integer getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Integer paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
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

    public List<TransactionDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<TransactionDetailResponse> details) {
        this.details = details;
    }
}
