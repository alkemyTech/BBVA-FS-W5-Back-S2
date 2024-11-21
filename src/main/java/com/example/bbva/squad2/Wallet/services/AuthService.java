package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private JwtServices jwtService;

    @Autowired
    private UserService usuarioService;

    public Map<String, Object> login(final String username, final String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Optional<User> usuarioOpt = usuarioService.getByUsername(username);
            if (usuarioOpt.isEmpty()) {
                throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
            }

            User usuario = usuarioOpt.get();
            response.put("id", usuario.getId());
            response.put("token", jwtService.generateToken(usuario.getId(), username, usuario.getRole().getName().name()));
            response.put("nombre", usuario.getFirstName());
            response.put("apellido", usuario.getLastName());
        } catch (Exception e) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas....");
        }
        return response;
    }


}