package toko_online.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import toko_online.model.enums.PaymentMethod;

public class TransactionRequest {

    @NotNull(message = "ID Produk wajib diisi")
    @Min(value = 1, message = "ID Produk harus lebih dari 0")
    private Integer productId;

    @NotNull(message = "Kuantitas wajib diisi")
    @Min(value = 1, message = "Kuantitas pembelian minimal 1")
    private Integer quantity;

    @NotBlank(message = "Email pembeli wajib diisi")
    @Email(message = "Format email pembeli tidak valid")
    private String userEmail;

    @NotNull(message = "Jumlah uang pembayaran wajib diisi")
    @Min(value = 1, message = "Jumlah pembayaran harus lebih dari 0")
    private Integer paidAmount;

    private PaymentMethod paymentMethod;

    public TransactionRequest() {
    }

    public TransactionRequest(Integer productId, Integer quantity, String userEmail, Integer paidAmount,
            PaymentMethod paymentMethod) {
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
