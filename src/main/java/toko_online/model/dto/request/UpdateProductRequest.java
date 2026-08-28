package toko_online.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateProductRequest {

    @NotBlank(message = "Nama produk wajib diisi")
    private String name;

    @NotNull(message = "Harga produk wajib diisi")
    @Min(value = 0, message = "Harga produk tidak boleh negatif")
    private Double price;

    @NotNull(message = "Stok produk wajib diisi")
    @Min(value = 0, message = "Stok produk tidak boleh negatif")
    private Integer stock;

    public UpdateProductRequest(String name, Double price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
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
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
