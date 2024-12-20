package com.example.bbva.squad2.Wallet.config;

import com.example.bbva.squad2.Wallet.enums.Concept;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.RolesRepository;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository usuarioRepository,
                                      RolesRepository roleRepository,
                                      AccountsRepository accountRepository,
                                      AccountService accountService,
                                      TransactionsRepository tr,
                                      UserService us) {
        return args -> {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (usuarioRepository.count() == 0) {
                // Crear el rol de ADMIN si no existe
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now())));

                // Crear el rol de USER si no existe
                Role userRole = roleRepository.findByName(RoleName.USER)
                        .orElseGet(() -> roleRepository.save(
                                new Role(RoleName.USER, "User role", LocalDateTime.now(), LocalDateTime.now())));

                // Crear usuarios ADMIN y REGULAR sin limitaciones
                String[][] adminUsers = {
                        {"Pepe", "Giménez", "pepe.gimenez@yopmail.com", "Pepe@2024Gimenez!"},
                        {"Juan", "Pérez", "juan.perez@yopmail.com", "JuanP@2024Perez!"},
                        {"Ana", "Martínez", "ana.martinez@yopmail.com", "Ana_M@2024Martinez#"},
                        {"Carlos", "López", "carlos.lopez@yopmail.com", "Carlos!2024Lopez@"},
                        {"Marta", "Fernández", "marta.fernandez@yopmail.com", "Marta2024_Fernandez!"}
                };

// Crear 10 usuarios regulares con nombres variados y correos realistas
                String[][] regularUsers = {
                        {"Pedro", "Ruiz", "juancruzcaggiano@hotmail.com", "Pedro!2024Ruiz#"},
                        {"María", "García", "maria.garcia@yopmail.com", "Maria@2024Garcia!"},
                        {"Fernando", "Jiménez", "fernando.jimenez@yopmail.com", "Fernando2024!Jimenez#"},
                        {"Carmen", "Álvarez", "carmen.alvarez@yopmail.com", "Carmen!2024Alvarez#"},
                        {"Rafael", "Moreno", "rafael.moreno@yopmail.com", "Rafael2024_Moreno!"}
                };

                // Crear los usuarios ADMIN
                for (String[] userData : adminUsers) {
                    String firstName = userData[0];
                    String lastName = userData[1];
                    String email = userData[2];
                    String password = userData[3];

                    // Hash de la contraseña
                    String hashedPassword = passwordEncoder.encode(password);

                    User adminUser = usuarioRepository.save(
                            User.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .email(email)
                                    .password(hashedPassword)  // Contraseña con hash
                                    .role(adminRole)
                                    .creationDate(LocalDateTime.now())
                                    .updateDate(LocalDateTime.now())
                                    .build());

                    // Crear cuentas en dólares (USD) y pesos (ARS) para el usuario ADMIN
                    createAccountForUser(accountService, accountRepository, adminUser, tr);
                }

                // Crear los usuarios REGULAR
                for (String[] userData : regularUsers) {
                    String firstName = userData[0];
                    String lastName = userData[1];
                    String email = userData[2];
                    String password = userData[3];

                    // Hash de la contraseña
                    String hashedPassword = passwordEncoder.encode(password);

                    User regularUser = usuarioRepository.save(
                            User.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .email(email)
                                    .password(hashedPassword)  // Contraseña con hash
                                    .role(userRole)
                                    .creationDate(LocalDateTime.now())
                                    .updateDate(LocalDateTime.now())
                                    .build());

                    // Crear cuentas en dólares (USD) y pesos (ARS) para el usuario REGULAR
                    createAccountForUser(accountService, accountRepository, regularUser, tr);
                }

                // Aquí puedes agregar la lógica para las transferencias, si es necesario

            }
        };
    }

    private void createAccountForUser(AccountService accountService, AccountsRepository accountRepository, User user, TransactionsRepository tr) {
        // Crear cuentas en dólares (USD) y pesos (ARS) para cada usuario
        Account accountPesos = accountRepository.save(
                Account.builder()
                        .cbu(accountService.generaCBU())
                        .currency(CurrencyTypeEnum.ARS)
                        .transactionLimit(300000.0)
                        .balance(10000.0)
                        .user(user)
                        .build());

        Account accountDolares = accountRepository.save(
                Account.builder()
                        .cbu(accountService.generaCBU())
                        .currency(CurrencyTypeEnum.USD)
                        .transactionLimit(1000.0)
                        .balance(10000.0)
                        .user(user)
                        .build());

        // Crear depósitos (puedes quitar el límite si no lo necesitas)
        tr.save(
                Transaction.builder()
                        .CbuDestino(accountPesos.getCbu())
                        .CbuOrigen("External")
                        .concept(Concept.Otros)
                        .description("Deposito")
                        .timestamp(LocalDateTime.now())
                        .amount(5000.00)
                        .account(accountDolares)
                        .type(TransactionTypeEnum.Deposito)
                        .build());

        tr.save(
                Transaction.builder()
                        .CbuDestino(accountPesos.getCbu())
                        .CbuOrigen("External")
                        .concept(Concept.Otros)
                        .description("Deposito")
                        .timestamp(LocalDateTime.now())
                        .amount(15000.00)
                        .account(accountPesos)
                        .type(TransactionTypeEnum.Deposito)
                        .build());
    }
}



