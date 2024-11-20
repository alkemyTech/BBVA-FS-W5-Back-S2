package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }


    //Comento hasta que tengamos el autenticador
    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        String currentUsername = authentication.getName();
        boolean isAdmin = userService.isAdmin(currentUsername);

        userService.deleteUser(id, currentUsername, isAdmin);
        return ResponseEntity.noContent().build();
    }*/
}
