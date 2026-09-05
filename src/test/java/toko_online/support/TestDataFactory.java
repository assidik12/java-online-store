package toko_online.support;

import toko_online.model.dto.request.LoginRequest;
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.request.RegisterRequest;
import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.request.UpdateProductRequest;
import toko_online.model.entity.Product;
import toko_online.model.entity.User;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.Role;

import java.util.concurrent.atomic.AtomicLong;

public class TestDataFactory {

    private static final AtomicLong ID_GEN = new AtomicLong(100);

    public static User createUser(String username, String email, Role role) {
        User user = new User(
                username,
                "$2a$12$eACCYoNO38qC46n4x6kQPOYJmN4JzBfN3d0Qp6qXF0Q8f7qQe9yOm", // valid dummy bcrypt hash
                email,
                role,
                "08123456789",
                "Jl. Merdeka No. 45",
                "12345"
        );
        user.setId(ID_GEN.incrementAndGet());
        return user;
    }

    public static User adminUser() {
        return createUser("admin_user", "admin@store.com", Role.ADMIN);
    }

    public static User regularUser() {
        return createUser("regular_user", "user@store.com", Role.USER);
    }

    public static Product createProduct(String name, Double price, Integer stock) {
        Product product = new Product(name, price, stock);
        product.setId(ID_GEN.intValue());
        return product;
    }

    public static Product inStockProduct(int stock) {
        return createProduct("Keyboard Mechanical", 350000.0, stock);
    }

    public static Product outOfStockProduct() {
        return createProduct("Mouse Gaming", 250000.0, 0);
    }

    public static RegisterRequest validRegisterRequest(String username, String email) {
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword("Secret123!");
        req.setPhoneNumber("08129876543");
        req.setAddress("Jl. Sudirman 10");
        req.setPosCode("54321");
        return req;
    }

    public static LoginRequest validLoginRequest(String identifier, String password) {
        LoginRequest req = new LoginRequest();
        req.setUserEmail(identifier);
        req.setPassword(password);
        return req;
    }

    public static ProductRequest validProductRequest(String name, Double price, Integer stock) {
        ProductRequest req = new ProductRequest();
        req.setName(name);
        req.setPrice(price);
        req.setStock(stock);
        return req;
    }

    public static UpdateProductRequest validUpdateProductRequest(String name, Double price, Integer stock) {
        return new UpdateProductRequest(name, price, stock);
    }

    public static TransactionRequest validTransactionRequest(Integer productId, Integer qty, String userEmail, Integer paidAmount) {
        TransactionRequest req = new TransactionRequest();
        req.setProductId(productId);
        req.setQuantity(qty);
        req.setUserEmail(userEmail);
        req.setPaidAmount(paidAmount);
        req.setPaymentMethod(PaymentMethod.CASH);
        return req;
    }
}
