package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
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

    public Map<String, Object> login(final String username, final String password) throws WalletsException{
        Map<String, Object> response = new HashMap<>();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Optional<User> usuarioOpt = usuarioService.getByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new WalletsException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
        }

        User usuario = usuarioOpt.get();
        if (usuario.getSoftDelete() != null) {
            throw new WalletsException(HttpStatus.UNAUTHORIZED, "Este usuario ha sido eliminado");
        }
        response.put("id", usuario.getId());
        response.put("token", jwtService.generateToken(usuario.getId(), username, usuario.getRole().getName().name()));
        response.put("nombre", usuario.getFirstName());
        response.put("apellido", usuario.getLastName());

        return response;
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName().name());
    }

}