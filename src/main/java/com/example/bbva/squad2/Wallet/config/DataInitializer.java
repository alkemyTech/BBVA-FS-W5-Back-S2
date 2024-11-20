package com.example.bbva.squad2.Wallet.config;

import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository usuarioRepository, RolesRepository roleRepository) {
        return args -> {

            // Verifica si ya existen usuarios en la base de datos
            if (usuarioRepository.count() == 0) {
                // Crear los roles si no existen
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now())));

                /*Role userRole = roleRepository.findByName(RoleName.USER)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.USER, "User role", LocalDateTime.now(), LocalDateTime.now())));*/

                // Crear el usuario con los roles
                usuarioRepository.save(
                        User.builder()
                                .firstName("Juan Cruz")
                                .lastName("Caggiano")
                                .email("juancruz.caggiano@bbva.com")
                                .password("$2a$10$UGaZsxous81YRRP3aDIyQu1P/lrVd0nKxxqIcz9LQAxHalz3V9eWa") // Contraseña ya encriptada
                                .roles(new HashSet<>(List.of(adminRole))) // Asignar múltiples roles
                                .creationDate(LocalDateTime.now())  // Establecer fecha de creación
                                .updateDate(LocalDateTime.now())    // Establecer fecha de actualización
                                .build());
            }
        };
    }
}


