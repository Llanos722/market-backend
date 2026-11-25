package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.service.AuthService;
import com.tecnm.merida.market_backend.web.dto.LoginRequest;
import com.tecnm.merida.market_backend.web.dto.LoginResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @SecurityRequirements
    public LoginResponse login(@RequestBody LoginRequest request) {
        return new LoginResponse(service.login(request.idCliente(), request.password()));
    }
}
