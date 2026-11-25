package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.web.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final ClienteCrudRepository clients;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final String dummyHash;

    public AuthService(ClienteCrudRepository clients, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.clients = clients;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.dummyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    public String login(String idCliente, String password) {
        if (idCliente == null || idCliente.isBlank() || password == null
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw unauthorized();
        }
        var client = clients.findById(idCliente).orElse(null);
        String hash = client == null ? null : client.getPassword();
        // Ejecuta BCrypt tambien si no existe el cliente para no revelar su existencia por tiempo de respuesta.
        boolean matches = passwordEncoder.matches(password, hash == null ? dummyHash : hash);
        if (hash == null || !matches) throw unauthorized();
        return jwtUtil.generateToken(client.getId());
    }

    private BadCredentialsException unauthorized() {
        return new BadCredentialsException("Credenciales invalidas");
    }
}
