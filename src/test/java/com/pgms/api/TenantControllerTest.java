package com.pgms.api;

import com.pgms.service.TenantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
class TenantControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    TenantService svc;

    @Test
    void createTenant_ok() throws Exception {
        Mockito.when(svc.create(Mockito.any())).thenReturn(UUID.randomUUID());
        String body = "{\"orgCode\":\"v2-colive\",\"fullName\":\"Test\",\"phone\":\"9876543210\"}";
        mvc.perform(post("/api/v1/tenants").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
    }
}
