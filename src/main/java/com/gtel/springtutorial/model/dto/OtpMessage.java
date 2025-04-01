package com.gtel.springtutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpMessage implements Serializable {
    private String phoneNumber;
    private String otp;
}