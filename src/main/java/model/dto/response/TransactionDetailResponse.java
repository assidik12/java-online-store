package model.dto.response;

public class TransactionDetailResponse {
    private Integer id;
    private String transactionId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer totalPriceProduct;
    private String formattedTotalPriceProduct;

    public TransactionDetailResponse() {
    }

    public TransactionDetailResponse(Integer id, String transactionId, Integer productId, String productName, Integer quantity, Integer totalPriceProduct) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPriceProduct = totalPriceProduct;
        this.formattedTotalPriceProduct = totalPriceProduct != null ? String.format("%,d", totalPriceProduct).replace(',', '.') : "0";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTotalPriceProduct() {
        return totalPriceProduct;
    }

    public void setTotalPriceProduct(Integer totalPriceProduct) {
        this.totalPriceProduct = totalPriceProduct;
        this.formattedTotalPriceProduct = totalPriceProduct != null ? String.format("%,d", totalPriceProduct).replace(',', '.') : "0";
    }

    public String getFormattedTotalPriceProduct() {
        return formattedTotalPriceProduct;
    }

    public void setFormattedTotalPriceProduct(String formattedTotalPriceProduct) {
        this.formattedTotalPriceProduct = formattedTotalPriceProduct;
    }
}
