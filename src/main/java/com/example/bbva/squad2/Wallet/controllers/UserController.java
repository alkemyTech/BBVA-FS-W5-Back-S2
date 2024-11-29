package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.PageableResponseDTO;
import com.example.bbva.squad2.Wallet.dtos.UserDTO;
import com.example.bbva.squad2.Wallet.dtos.UserUpdatedDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.services.UserService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UsuarioLoggeadoService usuarioLoggeadoService;

    @GetMapping
    @Operation(summary = "Buscar todos los usuarios")
    public List<UserDTO> getAllUsers() {

        return userService.getAllUsers();
    }

    //codeo la ful 46, es para paginar los usuarios
    @GetMapping("/paginated")
    @Operation(summary = "Obtener usuarios paginados", description = "Devuelve una lista paginada " +
            "de usuarios no eliminados.")

    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Validar valores de entrada
            if (page < 0 || size <= 0) {
                return ResponseEntity.badRequest().body("Los valores de página y tamaño deben " +
                        "ser positivos.");
            }

            // Llama al servicio para obtener los usuarios paginados
            PageableResponseDTO<UserDTO> paginatedUsers = userService.getAllUsersPaginated(page, size);
            return ResponseEntity.ok(paginatedUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener usuarios paginados.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuarios por Id")

    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            UsuarioSeguridad usuarioSeguridad = usuarioLoggeadoService.getInfoUserSecurity(request);

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
    @Operation(summary = "Buscar usuario loggeado por id")
    public ResponseEntity<UserDTO> getUserDetail(@PathVariable Long id, HttpServletRequest request) {
        try {
            UsuarioSeguridad usuarioSeguridad = usuarioLoggeadoService.getInfoUserSecurity(request);

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

    @PatchMapping("/")
    @Operation(summary = "Editar para el usuario loggeado")
    public ResponseEntity<String> updateUser(
            @RequestBody UserUpdatedDTO userUpdated,
            HttpServletRequest request) {
        UsuarioSeguridad user = usuarioLoggeadoService.getInfoUserSecurity(request);
        String result = userService.updateUser(user.getId(), userUpdated);

        if ("Usuario actualizado exitosamente.".equals(result)) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }


}
