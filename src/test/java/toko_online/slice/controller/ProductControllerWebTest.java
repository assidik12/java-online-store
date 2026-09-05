package toko_online.slice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import toko_online.controller.ProductController;
import toko_online.exception.ResourceNotFoundException;
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.response.ProductResponse;
import toko_online.security.AppUserDetailsService;
import toko_online.security.JwtAuthenticationFilter;
import toko_online.security.TokenProvider;
import toko_online.service.ProductService;
import toko_online.support.TestDataFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("GET /api/v1/products: returns 200 and product list")
    void getAllProducts_returns200() throws Exception {
        ProductResponse p1 = new ProductResponse(1, "Product A", 10000.0, 5);
        when(productService.getAllProducts()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Product A"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: found returns 200")
    void getProductById_found_returns200() throws Exception {
        ProductResponse p1 = new ProductResponse(10, "Product A", 10000.0, 5);
        when(productService.getProductById(10)).thenReturn(p1);

        mockMvc.perform(get("/api/v1/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}: not found returns 404")
    void getProductById_notFound_returns404() throws Exception {
        when(productService.getProductById(99)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/products: valid request returns 201")
    void createProduct_valid_returns201() throws Exception {
        ProductRequest req = TestDataFactory.validProductRequest("Keyboard", 250000.0, 10);
        ProductResponse res = new ProductResponse(1, "Keyboard", 250000.0, 10);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Keyboard"));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id}: success returns 200")
    void deleteProduct_success_returns200() throws Exception {
        when(productService.deleteProduct(5)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/products/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}
