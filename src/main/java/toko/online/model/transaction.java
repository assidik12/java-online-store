package toko.online.model;

import java.util.Date;

public class transaction {
    private String email;
    private int id_product;
    private String transactionId;
    private String payment_method;
    private int qty;
    private int total_price_product;
    private int total_price_amount;
    private Date date;
    private boolean status;

    public transaction(String email, int id_product, String transactionId, int qty, int total_price_product, int total_price_amount, Date date, boolean status, String payment_method) {
        this.transactionId = transactionId;
        this.total_price_amount = total_price_amount;
        this.qty = qty;
        this.total_price_product = total_price_product;
        this.date = date;
        this.status = status;
        this.email = email;
        this.id_product = id_product;
        this.payment_method = payment_method;
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayment_method() {
        return payment_method;
    }
    
    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public int getTotal_price_amount() {
        return total_price_amount;
    }

    public void setTotal_price_amount(int total_price_amount) {
        this.total_price_amount = total_price_amount;
    }

    public int getTotal_price_product() {
        return total_price_product;
    }

    public void setTotal_price_product(int total_price_product) {
        this.total_price_product = total_price_product;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }
}
