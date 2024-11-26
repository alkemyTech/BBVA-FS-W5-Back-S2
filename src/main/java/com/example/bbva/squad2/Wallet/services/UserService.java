package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.dtos.UserDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AccountService accountService;
    private final UserRepository userRepository;
    private final AccountsRepository accountsRepository;
    private final RolesRepository rolesRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private JwtServices js;

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

    public UserService(AccountService accountService, UserRepository userRepository, RolesRepository rolesRepository,
                       AccountsRepository accountsRepository) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.accountsRepository = accountsRepository;
    }

    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new AlkemyException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado."
                ));

        userToDelete.setSoftDelete(LocalDateTime.now());
        userRepository.save(userToDelete);
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .filter(u -> u.getSoftDelete() == null)
                .map(UserDTO::mapFromUser)
                .collect(Collectors.toList());
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
        Role role = rolesRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + RoleName.USER.name()));


        // Crear y guardar el usuario
        User newUser = User.builder()
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .email(registerDTO.getEmail())
                .password(encryptPassword(registerDTO.getPassword()))
                .role(role)
                .build();


        userRepository.save(newUser);

        return newUser;
    }

    private String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public UsuarioSeguridad getInfoUserSecurity(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        UsuarioSeguridad userSecurity = js.validateAndGetSecurity(token);

        if (userSecurity == null) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        return userSecurity;
    }



    public Optional<User> findById(Long id){
		return userRepository.findById(id);
	}

    // codeo ful 42 metodo para obtener detalle de usuario
    public UserDTO getUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AlkemyException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return UserDTO.mapFromUser(user);
    }
}





