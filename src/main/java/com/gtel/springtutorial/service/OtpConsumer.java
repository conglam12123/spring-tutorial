package com.gtel.springtutorial.service;

import com.gtel.springtutorial.config.RabbitMQConfig;
import com.gtel.springtutorial.model.dto.OtpMessage;
import com.gtel.springtutorial.model.entity.OtpEntity;
import com.gtel.springtutorial.repository.OtpRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OtpConsumer {
    private final OtpRepo otpRepo;

    @RabbitListener(queues = RabbitMQConfig.OTP_QUEUE)
    public void receiveOtp(OtpMessage otpMessage) {

        try {
            log.info("Consumer saved otp {} sent to {}", otpMessage.getOtp(), otpMessage.getPhoneNumber());

            OtpEntity otpEntity = new OtpEntity(otpMessage);
            otpRepo.save(otpEntity);
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
            // Không throw Exception để tránh message bị giữ lại trong queue
        }
    }

}
