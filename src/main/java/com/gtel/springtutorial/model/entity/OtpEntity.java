package com.gtel.springtutorial.model.entity;

import com.gtel.springtutorial.model.dto.OtpMessage;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Table(name = "otp_history")
@Entity
@Data
public class OtpEntity extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String otp;


    public OtpEntity (OtpMessage message) {
//        this.id = UUID.randomUUID().toString();
        this.phoneNumber = message.getPhoneNumber();
        this.otp  = message.getOtp();
    }

    public OtpEntity() {

    }
}
