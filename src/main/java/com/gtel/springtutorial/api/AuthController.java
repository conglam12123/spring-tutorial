package com.gtel.springtutorial.api;

import com.gtel.springtutorial.model.request.ChangePasswordRequest;
import com.gtel.springtutorial.model.request.RegisterRequest;
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
    public Object registerUser(@RequestBody(required = true)RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping(value = "/activate")
    public Object activateAccount(@RequestBody RegisterRequest request) {
        return authService.activateAccountWithOtp(request.getPhoneNumber(), request.getOtp());
    }

    @PostMapping(value = "/change-password")
    public Object changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.updatePassword(request.getPhoneNumber(), request.getOldPassword(), request.getNewPassword());
    }
}
