package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountsRepository accountsRepository;
    private final RolesRepository rolesRepository;


    public UserService(UserRepository userRepository,RolesRepository rolesRepository,
                       AccountsRepository accountsRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.accountsRepository = accountsRepository;
    }

    @Transactional
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

    //codigo hugo pertenece a ful22
    
    @Transactional
    public User registerUser(String firstName, String lastName, String email, String password) {

        // Validar si el email ya está registrado
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Buscar el rol USER
        Role userRole = rolesRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        // Crear y guardar el usuario
        User newUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(encryptPassword(password)) // encripto contraseña
                .role(userRole)
                .build();

        userRepository.save(newUser);

        // Crear las cuentas asociadas
        createAccount(newUser, CurrencyTypeEnum.ARS, 300000.0);
        createAccount(newUser, CurrencyTypeEnum.USD, 1000.0);

        return newUser;
    }

    private void createAccount(User user, CurrencyTypeEnum currency, Double transactionLimit) {
        Account account = Account.builder()
                .currency(currency)
                .transactionLimit(transactionLimit)
                .balance(0.0)
                .user(user)
                .build();

        accountsRepository.save(account);
    }

    private String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
