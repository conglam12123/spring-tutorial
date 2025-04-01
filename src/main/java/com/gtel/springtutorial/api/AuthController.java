package com.gtel.springtutorial.api;

import com.gtel.springtutorial.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth")
public class AuthController {
    final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    public Object registerUser(@RequestBody(required = true) String phoneNum) {
        return authService.register(phoneNum);
    }

    @PostMapping(value = "/activate")
    public Object activateAccount(@RequestParam(required = true) String phoneNumber, @RequestParam(required = true) String otp) {
        return authService.activateAccountWithOtp(phoneNumber, otp);
    }

    @PostMapping(value = "/change-password")
    public Object changePassword(@RequestParam(required = true) String phoneNumber, @RequestParam(required = true) String password) {
        return authService.updatePassword(phoneNumber, password);
    }
}
