package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtServices jwtServices;

    @Autowired
    public UserController(UserService userService, JwtServices jwtServices) {
        this.userService = userService;
        this.jwtServices = jwtServices;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Extraer el token JWT del header Authorization
            String token = request.getHeader("Authorization");
            token = token.substring(7);

            // Extraer el rol del token JWT
            String role = jwtServices.extractRole(token);

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = role.equals(RoleName.ADMIN.name());

            // Si no tiene rol ADMIN, lanzar una excepción de seguridad
            if (!isAdmin) {
                throw new SecurityException("Usted no esta autorizado para eliminar usuarios.");
            }

            // Obtener el nombre de usuario desde el token
            String currentUsername = jwtServices.extractUsername(token);

            // Llamar al servicio para eliminar el usuario
            userService.deleteUser(id, currentUsername, isAdmin);

            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            // Manejar el caso cuando el usuario no tiene permisos
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            // Manejar el caso cuando el usuario no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // Manejar cualquier otra excepción general
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado.");
        }
    }
}
