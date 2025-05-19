package com.gtel.springtutorial.model.request;

import lombok.Data;

@Data
public class LoginRequest {
    String password;
    String phoneNumber;
}
