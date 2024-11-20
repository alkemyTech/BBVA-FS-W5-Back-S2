package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountsRepository accountsRepository;
    private final RolesRepository rolesRepository;

    @Autowired
    private UserRepository usuarioRepository;

    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<User> optionalUser = usuarioRepository.findByEmail(username);

            User user = optionalUser.orElseThrow(()
                    -> new UsernameNotFoundException("User not found with email: " + username));

            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }

    public UserService(UserRepository userRepository,RolesRepository rolesRepository,
                       AccountsRepository accountsRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.accountsRepository = accountsRepository;
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
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == RoleName.ADMIN)) // Verificamos si alguno de los roles es ADMIN
                .orElse(false);
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getByUsername(final String username) {
        return usuarioRepository.findByEmail(username);
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
                .password(encryptPassword(password))
                .roles(Set.of(userRole))
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

    public Optional<User> findById(Long id){
		return userRepository.findById(id);
	}
}
