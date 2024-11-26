package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.UserDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
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
    public List<UserDTO> getAllUsers() {

        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            UsuarioSeguridad usuarioSeguridad = userService.getInfoUserSecurity(request);

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = usuarioSeguridad.getRole().equals(RoleName.ADMIN.name());

            // Si no tiene rol ADMIN, lanzar una excepción de seguridad
            if (!isAdmin) {
                throw new AlkemyException(
                        HttpStatus.FORBIDDEN,
                        "Usted no esta autorizado para eliminar usuarios."
                );
            }

            // Llamar al servicio para eliminar el usuario
            userService.deleteUser(id);

            return ResponseEntity.noContent().build();
        } catch (AlkemyException e) {
            // Manejar la excepción específica de falta de permisos
            return ResponseEntity.status(e.getStatus()).body(null);
        } catch (RuntimeException e) {
            // Manejar el caso cuando el usuario no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // Manejar cualquier otra excepción general
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado.");
        }
    }

    // comence a codear la ful 42 (hugo)

    @GetMapping("/{id}/")
    public ResponseEntity<UserDTO> getUserDetail(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Extraer el token JWT del header Authorization
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente.");
            }
            token = token.substring(7);

            // Extraer el rol del token JWT
            UsuarioSeguridad usuarioSeguridad = jwtServices.validateAndGetSecurity(token);

            // Verificar si el ID en la URL coincide con el ID del usuario logueado
            if (!usuarioSeguridad.getId().equals(id)) {
                throw new AlkemyException(HttpStatus.FORBIDDEN, "No tienes permisos para ver este usuario.");
            }

            // Llamar al servicio para obtener los detalles del usuario y devolver el UserDTO
            UserDTO userDTO = userService.getUserDetail(id);

            return ResponseEntity.ok(userDTO);

        } catch (AlkemyException e) {
            return ResponseEntity.status(e.getStatus()).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado.");
        }
    }

}
