package id.my.hendisantika.sagapatternsample.saga;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Project : saga-pattern-sample
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 25/10/26
 * Time: 13.21
 */
@SpringBootTest
@AutoConfigureMockMvc
class SagaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void orchestratedOrderReturnsFinalSagaState() throws Exception {
        mockMvc.perform(post("/api/orchestration/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId":"CUST-003","productCode":"CONSOLE-001","quantity":1,
                                 "amount":499.00,"shippingCity":"Jakarta"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sagaStatus").value("COMPENSATED"))
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"))
                .andExpect(jsonPath("$.failedStep").value("RESERVE_STOCK"));
    }

    @Test
    void choreographedOrderIsAcceptedAsPending() throws Exception {
        mockMvc.perform(post("/api/choreography/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId":"CUST-003","productCode":"LAPTOP-001","quantity":1,
                                 "amount":1200.00,"shippingCity":"Surabaya"}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.sagaType").value("CHOREOGRAPHY"));
    }

    @Test
    void rejectsInvalidOrder() throws Exception {
        mockMvc.perform(post("/api/orchestration/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId":"","productCode":"LAPTOP-001","quantity":0,"shippingCity":"Jakarta"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownSaga() throws Exception {
        mockMvc.perform(get("/api/orchestration/sagas/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
