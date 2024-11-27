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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository usuarioRepository,
                                      RolesRepository roleRepository,
                                      AccountsRepository accountRepository,
                                      AccountService accountService) {
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

                // Crear 10 usuarios admin con nombres variados y correos realistas
                String[][] adminUsers = {
                        {"Pepe", "Giménez", "pepe.gimenez@empresa.com", "Pepe@2024Gimenez!"},
                        {"Juan", "Pérez", "juan.perez@correo.com", "JuanP@2024Perez!"},
                        {"Ana", "Martínez", "ana.martinez@empresa.com", "Ana_M@2024Martinez#"},
                        {"Carlos", "López", "carlos.lopez@corporativo.com", "Carlos!2024Lopez@"},
                        {"Marta", "Fernández", "marta.fernandez@empresa.com", "Marta2024_Fernandez!"},
                        {"Luis", "Sánchez", "luis.sanchez@empresa.com", "Luis@2024Sanchez#"},
                        {"Raúl", "Díaz", "raul.diaz@empresa.com", "Raul2024D!az#"},
                        {"Lucía", "González", "lucia.gonzalez@correo.com", "Lucia@2024Gonzalez!"},
                        {"Sofía", "Rodríguez", "sofia.rodriguez@empresa.com", "Sofia2024_Rodriguez!"},
                        {"David", "Hernández", "david.hernandez@corporativo.com", "David2024!Hernandez@"}
                };

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
                    accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.USD)
                                    .transactionLimit(1000.0)
                                    .balance(10000.0)
                                    .user(adminUser)
                                    .build());

                    accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.ARS)
                                    .transactionLimit(300000.0)
                                    .balance(10000.0)
                                    .user(adminUser)
                                    .build());
                }

                // Crear 10 usuarios user con nombres variados y correos realistas
                String[][] regularUsers = {
                        {"Pedro", "Ruiz", "pedro.ruiz@gmail.com", "Pedro!2024Ruiz#"},
                        {"María", "García", "maria.garcia@gmail.com", "Maria@2024Garcia!"},
                        {"Fernando", "Jiménez", "fernando.jimenez@correo.com", "Fernando2024!Jimenez#"},
                        {"Carmen", "Álvarez", "carmen.alvarez@hotmail.com", "Carmen!2024Alvarez#"},
                        {"Rafael", "Moreno", "rafael.moreno@outlook.com", "Rafael2024_Moreno!"},
                        {"Isabel", "Gil", "isabel.gil@empresa.com", "Isabel!2024Gil@"},
                        {"Antonio", "Vázquez", "antonio.vazquez@gmail.com", "Antonio2024_Vazquez!"},
                        {"Raquel", "Romero", "raquel.romero@empresa.com", "Raquel@2024Romero!"},
                        {"José", "Martín", "jose.martin@correo.com", "Jose@2024Martin#"},
                        {"Patricia", "Serrano", "patricia.serrano@gmail.com", "Patricia2024!Serrano#"}
                };

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

                    // Crear cuentas en dólares (USD) y pesos (ARS) para el usuario regular
                    accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.USD)
                                    .transactionLimit(1000.0)
                                    .balance(10000.0)
                                    .user(regularUser)
                                    .build());

                    accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.ARS)
                                    .transactionLimit(300000.0)
                                    .balance(10000.0)
                                    .user(regularUser)
                                    .build());
                }
            }
        };
    }
}
