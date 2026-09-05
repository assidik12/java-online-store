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
import toko_online.controller.TransactionController;
import toko_online.exception.InsufficientStockException;
import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.TransactionResponse;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.TransactionStatus;
import toko_online.security.AppUserDetailsService;
import toko_online.security.JwtAuthenticationFilter;
import toko_online.security.TokenProvider;
import toko_online.service.TransactionService;
import toko_online.support.TestDataFactory;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/v1/transactions: success returns 201 Created")
    void buyProduct_valid_returns201() throws Exception {
        TransactionRequest req = TestDataFactory.validTransactionRequest(1, 2, "buyer@mail.com", 100000);
        TransactionResponse res = new TransactionResponse("tx-123", "buyer@mail.com", 80000, 100000, 20000,
                TransactionStatus.PAID, new Date(), PaymentMethod.CASH, List.of());
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").value("tx-123"));
    }

    @Test
    @DisplayName("POST /api/v1/transactions: insufficient stock returns 409 Conflict")
    void buyProduct_insufficientStock_returns409() throws Exception {
        TransactionRequest req = TestDataFactory.validTransactionRequest(1, 20, "buyer@mail.com", 500000);
        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenThrow(new InsufficientStockException("Stok tidak mencukupi"));

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/transactions: returns list of transactions")
    void getTransactions_returns200() throws Exception {
        TransactionResponse res = new TransactionResponse("tx-10", "buyer@mail.com", 50000, 50000, 0,
                TransactionStatus.PAID, new Date(), PaymentMethod.CASH, List.of());
        when(transactionService.getTransactionsByUserEmail(eq(null))).thenReturn(List.of(res));

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].transactionId").value("tx-10"));
    }
}
