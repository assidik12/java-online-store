package model.dto.response;

public class ProductResponse {
    private Integer id;
    private String name;
    private Double price;
    private Integer stock;
    private String formattedPrice;

    public ProductResponse() {
    }

    public ProductResponse(Integer id, String name, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.formattedPrice = price != null ? String.format("Rp. %,.2f", price) : "Rp. 0,00";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.formattedPrice = price != null ? String.format("Rp. %,.2f", price) : "Rp. 0,00";
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }
}
