package com.gtel.springtutorial.api;

import com.gtel.springtutorial.model.request.ChangePasswordRequest;
import com.gtel.springtutorial.model.request.RegisterRequest;
import com.gtel.springtutorial.model.response.RegisterResponse;
import com.gtel.springtutorial.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth")
public class AuthController {
    final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    public RegisterResponse registerUser(@RequestBody(required = true)RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<String> activateAccount(@RequestBody RegisterRequest request) {
        return authService.activateAccountWithOtp(request.getPhoneNumber(), request.getOtp());
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.updatePassword(request.getPhoneNumber(), request.getOldPassword(), request.getNewPassword());
    }
}
