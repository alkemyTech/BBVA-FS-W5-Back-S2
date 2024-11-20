package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*@Transactional
    public void deleteUser(Long userId, String currentUsername, boolean isAdmin) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar permisos
        if (!isAdmin && !userToDelete.getEmail().equals(currentUsername)) {
            throw new SecurityException("You are not allowed to delete this user.");
        }

        // Realizar eliminación lógica
        userToDelete.setSoftDelete(LocalDateTime.now());
        userRepository.save(userToDelete);
    }*/

    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Realizar eliminación lógica
        userToDelete.setSoftDelete(LocalDateTime.now());
        userRepository.save(userToDelete);
    }

    public boolean isAdmin(String currentUsername) {
        return userRepository.findByEmail(currentUsername)
                .map(User::getRole)
                .map(role -> role.getName() == RoleName.ADMIN)
                .orElse(false);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id){
		return userRepository.findById(id);
	}
}
