package com.example.bbva.squad2.Wallet.config;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import com.example.bbva.squad2.Wallet.services.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository usuarioRepository,
                                      RolesRepository roleRepository,
                                      AccountsRepository accountRepository,
                                      AccountService accountService) {
        return args -> {

            if (usuarioRepository.count() == 0) {
                // Crear el rol de ADMIN si no existe
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now())));

                // Crear el rol de USER si no existe
                Role userRole = roleRepository.findByName(RoleName.USER)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.USER, "User role", LocalDateTime.now(), LocalDateTime.now())));

                // Crear el usuario ADMIN
                User adminUser = usuarioRepository.save(
                        User.builder()
                                .firstName("Juan Cruz")
                                .lastName("Caggiano")
                                .email("juancruz.caggiano@bbva.com")
                                .password("$2a$10$UGaZsxous81YRRP3aDIyQu1P/lrVd0nKxxqIcz9LQAxHalz3V9eWa")
                                .role(adminRole)
                                .creationDate(LocalDateTime.now())
                                .build());

                // Crear cuenta en dólares para el usuario ADMIN
                accountRepository.save(
                        Account.builder()
                                .cbu(accountService.generaCBU())
                                .currency(CurrencyTypeEnum.USD)
                                .transactionLimit(1000.0)
                                .balance(10000.0)
                                .user(adminUser)
                                .build());

                // Crear cuenta en pesos para el usuario ADMIN
                accountRepository.save(
                        Account.builder()
                                .cbu(accountService.generaCBU())
                                .currency(CurrencyTypeEnum.ARS)
                                .transactionLimit(300000.0)
                                .balance(10000.0)
                                .user(adminUser)
                                .build());

                // Crear el usuario USER
                User randomUser = usuarioRepository.save(
                        User.builder()
                                .firstName("Hugo")
                                .lastName("Cozzani")
                                .email("hugocozzani@example.com")
                                .password("$2a$10$UGaZsxous81YRRP3aDIyQu1P/lrVd0nKxxqIcz9LQAxHalz3V9eWa")
                                .role(userRole)
                                .creationDate(LocalDateTime.now())
                                .build());

                // Crear cuenta en dólares para el usuario USER
                accountRepository.save(
                        Account.builder()
                                .cbu(accountService.generaCBU())
                                .currency(CurrencyTypeEnum.USD)
                                .transactionLimit(1000.0)
                                .balance(10000.0)
                                .user(randomUser)
                                .build());

                // Crear cuenta en pesos para el usuario USER
                accountRepository.save(
                        Account.builder()
                                .cbu(accountService.generaCBU())
                                .currency(CurrencyTypeEnum.ARS)
                                .transactionLimit(300000.0)
                                .balance(10000.0)
                                .user(randomUser)
                                .build());
            }
        };
    }
}
