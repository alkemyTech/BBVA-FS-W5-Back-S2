package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UsuarioLoggeadoService {

    @Autowired
    private JwtServices js;

    public UsuarioSeguridad getInfoUserSecurity(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        UsuarioSeguridad userSecurity = js.validateAndGetSecurity(token);

        if (userSecurity == null) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        return userSecurity;
    }
}
