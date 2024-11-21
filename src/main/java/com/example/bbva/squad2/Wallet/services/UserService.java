package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
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

            User user = optionalUser.orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + username));

            // Obtener el único rol del usuario
            Role role = user.getRole(); // Cambiado de getRoles() a getRole()

            // Crear una autoridad basada en el único rol
            GrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());

            // Devolver el usuario con la autoridad
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(List.of(authority)) // Asignar solo una autoridad
                    .build();
        };
    }


    public UserService(UserRepository userRepository,RolesRepository rolesRepository,
                       AccountsRepository accountsRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.accountsRepository = accountsRepository;
    }

    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        userToDelete.setSoftDelete(LocalDateTime.now());
        userRepository.save(userToDelete);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getByUsername(final String username) {
        return usuarioRepository.findByEmail(username);
    }

    //codigo hugo pertenece a ful22

    @Transactional
    public User registerUser(RegisterDTO registerDTO) {

        // Validar si el email ya está registrado
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        // Buscar el rol por el nombre especificado en el DTO
        Role role = rolesRepository.findByName(RoleName.valueOf(registerDTO.getRole()))
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + registerDTO.getRole()));

        // Crear las cuentas desde los AccountDTO
        List<Account> accounts = registerDTO.getAccounts().stream()
                .map(accountDTO -> Account.builder()
                        .currency(accountDTO.getCurrency()) // Adaptar según cómo esté definido
                        .transactionLimit(accountDTO.getTransactionLimit())
                        .balance(accountDTO.getBalance())
                        .build())
                .collect(Collectors.toList());

        // Crear y guardar el usuario
        User newUser = User.builder()
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .email(registerDTO.getEmail())
                .password(encryptPassword(registerDTO.getPassword()))
                .role(role)
                .accounts(accounts)
                .build();

        // Guardar el usuario y sus cuentas
        userRepository.save(newUser);
        accounts.forEach(account -> account.setUser(newUser));
        accountsRepository.saveAll(accounts);

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
