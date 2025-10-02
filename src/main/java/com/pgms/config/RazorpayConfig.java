package com.pgms.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(
            @Value("${payments.razorpay.keyId}") String keyId,
            @Value("${payments.razorpay.keySecret}") String keySecret) throws Exception {
        return new RazorpayClient(keyId, keySecret);
    }
}
