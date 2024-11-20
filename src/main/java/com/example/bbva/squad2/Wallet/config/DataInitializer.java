package com.example.bbva.squad2.Wallet.config;

import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository usuarioRepository, RolesRepository roleRepository) {
        return args -> {

            // Verifica si ya existen usuarios en la base de datos
            if (usuarioRepository.count() == 0) {
                // Crear el rol de administrador si no existe
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now())));

                // Crear el usuario con un solo rol
                usuarioRepository.save(
                        User.builder()
                                .firstName("Juan Cruz")
                                .lastName("Caggiano")
                                .email("juancruz.caggiano@bbva.com")
                                .password("$2a$10$UGaZsxous81YRRP3aDIyQu1P/lrVd0nKxxqIcz9LQAxHalz3V9eWa") // Contraseña ya encriptada
                                .role(adminRole)
                                .creationDate(LocalDateTime.now())
                                .build());
            }
        };
    }
}
