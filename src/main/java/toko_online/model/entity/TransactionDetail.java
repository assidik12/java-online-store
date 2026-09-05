package toko_online.model.entity;

public class TransactionDetail {
    private Integer id;
    private String transactionId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer totalPriceProduct;

    public TransactionDetail() {
    }

    public TransactionDetail(String transactionId, Integer productId, Integer quantity, Integer totalPriceProduct) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPriceProduct = totalPriceProduct;
    }

    public TransactionDetail(Integer id, String transactionId, Integer productId, String productName, Integer quantity,
            Integer totalPriceProduct) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPriceProduct = totalPriceProduct;
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
    }
}
