package com.gtel.springtutorial.service;

import com.gtel.springtutorial.config.RabbitMQConfig;
import com.gtel.springtutorial.model.dto.OtpMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpProducer {
    private final RabbitTemplate rabbitTemplate;


    public void sendOtp(String phoneNumber, String otp) {
        OtpMessage message = new OtpMessage(phoneNumber, otp);

        rabbitTemplate.convertAndSend(RabbitMQConfig.OTP_QUEUE, message);
        log.info("SendOtp {} to {}", otp, phoneNumber);

    }


}
