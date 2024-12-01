package com.example.bbva.squad2.Wallet.config;

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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


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

                // Crear 10 usuarios admin con nombres variados y correos realistas
                String[][] adminUsers = {
                        {"Pepe", "Giménez", "pepe.gimenez@yopmail.com", "Pepe@2024Gimenez!"},
                        {"Juan", "Pérez", "juan.perez@yopmail.com", "JuanP@2024Perez!"},
                        {"Ana", "Martínez", "ana.martinez@yopmail.com", "Ana_M@2024Martinez#"},
                        {"Carlos", "López", "carlos.lopez@yopmail.com", "Carlos!2024Lopez@"},
                        {"Marta", "Fernández", "marta.fernandez@yopmail.com", "Marta2024_Fernandez!"},
                        {"Luis", "Sánchez", "luis.sanchez@yopmail.com", "Luis@2024Sanchez#"},
                        {"Raúl", "Díaz", "raul.diaz@yopmail.com", "Raul2024D!az#"},
                        {"Lucía", "González", "lucia.gonzalez@yopmail.com", "Lucia@2024Gonzalez!"},
                        {"Sofía", "Rodríguez", "sofia.rodriguez@yopmail.com", "Sofia2024_Rodriguez!"},
                        {"David", "Hernández", "david.hernandez@yopmail.com", "David2024!Hernandez@"}
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
                    Account accountPesos = accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.ARS)
                                    .transactionLimit(300000.0)
                                    .balance(10000.0)
                                    .user(adminUser)
                                    .build());

                    Account accountDolares = accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.USD)
                                    .transactionLimit(1000.0)
                                    .balance(10000.0)
                                    .user(adminUser)
                                    .build());

                    // Crear una transacción de tipo DEPOSITO para los usuarios ADMIN
                    tr.save(
                            Transaction.builder()
                                    .CbuDestino(accountPesos.getCbu())
                                    .CbuOrigen("External")
                                    .description("Deposito Inicial USD")
                                    .timestamp(LocalDateTime.now())
                                    .amount(5000.00)
                                    .account(accountDolares)
                                    .type(TransactionTypeEnum.DEPOSITO)
                                    .build());

                    tr.save(
                            Transaction.builder()
                                    .CbuDestino(accountPesos.getCbu())
                                    .CbuOrigen("External")
                                    .description("Deposito Inicial ARS")
                                    .timestamp(LocalDateTime.now())
                                    .amount(15000.00)
                                    .account(accountPesos)
                                    .type(TransactionTypeEnum.DEPOSITO)
                                    .build());


                }

                // Crear 10 usuarios user con nombres variados y correos realistas
                String[][] regularUsers = {
                        {"Pedro", "Ruiz", "pedro.ruiz@yopmail.com", "Pedro!2024Ruiz#"},
                        {"María", "García", "maria.garcia@yopmail.com", "Maria@2024Garcia!"},
                        {"Fernando", "Jiménez", "fernando.jimenez@yopmail.com", "Fernando2024!Jimenez#"},
                        {"Carmen", "Álvarez", "carmen.alvarez@yopmail.com", "Carmen!2024Alvarez#"},
                        {"Rafael", "Moreno", "rafael.moreno@yopmail.com", "Rafael2024_Moreno!"},
                        {"Isabel", "Gil", "isabel.gil@yopmail.com", "Isabel!2024Gil@"},
                        {"Antonio", "Vázquez", "antonio.vazquez@yopmail.com", "Antonio2024_Vazquez!"},
                        {"Raquel", "Romero", "raquel.romero@yopmail.com", "Raquel@2024Romero!"},
                        {"José", "Martín", "jose.martin@yopmail.com", "Jose@2024Martin#"},
                        {"Patricia", "Serrano", "patricia.serrano@yopmail.com", "Patricia2024!Serrano#"}
                };

                int maxDeposits = 1;  // Limite total de depósitos
                int depositCount = 0;  // Contador global de depósitos

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
                    Account accountPesos = accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.ARS)
                                    .transactionLimit(300000.0)
                                    .balance(10000.0)
                                    .user(regularUser)
                                    .build());

                    Account accountDolares = accountRepository.save(
                            Account.builder()
                                    .cbu(accountService.generaCBU())
                                    .currency(CurrencyTypeEnum.USD)
                                    .transactionLimit(1000.0)
                                    .balance(10000.0)
                                    .user(regularUser)
                                    .build());

                    // Crear depósitos solo si no se ha alcanzado el límite total
                    if (depositCount < maxDeposits) {
                        tr.save(
                                Transaction.builder()
                                        .CbuDestino(accountPesos.getCbu())
                                        .CbuOrigen("External")
                                        .description("Deposito Inicial USD")
                                        .timestamp(LocalDateTime.now())
                                        .amount(5000.00)
                                        .account(accountDolares)
                                        .type(TransactionTypeEnum.DEPOSITO)
                                        .build());

                        tr.save(
                                Transaction.builder()
                                        .CbuDestino(accountPesos.getCbu())
                                        .CbuOrigen("External")
                                        .description("Deposito Inicial ARS")
                                        .timestamp(LocalDateTime.now())
                                        .amount(15000.00)
                                        .account(accountPesos)
                                        .type(TransactionTypeEnum.DEPOSITO)
                                        .build());

                        depositCount++;  // Incrementa el contador de depósitos
                    } else {
                        System.out.println("Se ha alcanzado el límite de depósitos.");
                    }
                }



                int maxTransfers = 10;
                    int transferCount = 0;  // Contador de transferencias

                    List<Account> allAccounts = accountRepository.findAll();

                    List<Account> arsAccounts = allAccounts.stream()
                            .filter(a -> a.getCurrency() == CurrencyTypeEnum.ARS)
                            .toList();

                    for (int i = 0; i < arsAccounts.size(); i++) {
                        // Comprobación si se ha alcanzado el límite antes de procesar la cuenta
                        if (transferCount >= maxTransfers) {
                            System.out.println("Se ha alcanzado el límite máximo de transferencias.");
                            break;  // Salir del bucle si se alcanzó el límite
                        }

                        Account originAccount = arsAccounts.get(i);

                        List<Account> filteredAccounts = arsAccounts.stream()
                                .filter(dest -> !dest.getUser().getId().equals(originAccount.getUser().getId())) // Filtrar diferente usuario
                                .toList();

                        if (!filteredAccounts.isEmpty()) {
                            Account destinationAccount = null;

                            // Selección aleatoria manual con validación
                            for (int j = 0; j < filteredAccounts.size(); j++) {
                                int randomIndex = ThreadLocalRandom.current().nextInt(filteredAccounts.size());
                                destinationAccount = filteredAccounts.get(randomIndex);
                                if (destinationAccount != null) {
                                    break; // Si se encuentra una cuenta válida, salir del bucle
                                }
                            }

                            if (destinationAccount != null) {
                                Transaction transferTransactionIngreso = Transaction.builder()
                                        .CbuOrigen(originAccount.getCbu())
                                        .CbuDestino(destinationAccount.getCbu())
                                        .description("Transferencia entre cuentas ARS")
                                        .timestamp(LocalDateTime.now())
                                        .amount(1000.00)
                                        .account(originAccount)
                                        .type(TransactionTypeEnum.INGRESO)
                                        .build();
                                tr.save(transferTransactionIngreso);

                                Transaction transferTransactionPago = Transaction.builder()
                                        .CbuOrigen(originAccount.getCbu())
                                        .CbuDestino(destinationAccount.getCbu())
                                        .description("Transferencia entre cuentas ARS")
                                        .timestamp(LocalDateTime.now())
                                        .amount(1000.00)
                                        .account(destinationAccount)
                                        .type(TransactionTypeEnum.PAGO)
                                        .build();
                                tr.save(transferTransactionPago);

                                transferCount++;  // Incrementa el contador de transferencias
                            } else {
                                System.out.println("No se encontró una cuenta destino válida para el usuario: " +
                                        originAccount.getUser().getFirstName());
                            }
                        }
                    }

                    List<Account> usdAccounts = allAccounts.stream()
                            .filter(a -> a.getCurrency() == CurrencyTypeEnum.USD)
                            .toList();

                    for (int i = 0; i < usdAccounts.size(); i++) {
                        // Comprobación si se ha alcanzado el límite antes de procesar la cuenta
                        if (transferCount >= maxTransfers) {
                            System.out.println("Se ha alcanzado el límite máximo de transferencias.");
                            break;  // Salir del bucle si se alcanzó el límite
                        }

                        Account originAccount = usdAccounts.get(i);

                        List<Account> filteredAccounts = usdAccounts.stream()
                                .filter(dest -> !dest.getUser().getId().equals(originAccount.getUser().getId())) // Filtrar diferente usuario
                                .toList();

                        if (!filteredAccounts.isEmpty()) {
                            Account destinationAccount = null;

                            // Selección aleatoria manual con validación
                            for (int j = 0; j < filteredAccounts.size(); j++) {
                                int randomIndex = ThreadLocalRandom.current().nextInt(filteredAccounts.size());
                                destinationAccount = filteredAccounts.get(randomIndex);
                                if (destinationAccount != null) {
                                    break; // Si se encuentra una cuenta válida, salir del bucle
                                }
                            }

                            if (destinationAccount != null) {
                                Transaction transferTransactionIngreso = Transaction.builder()
                                        .CbuOrigen(originAccount.getCbu())
                                        .CbuDestino(destinationAccount.getCbu())
                                        .description("Transferencia entre cuentas USD")
                                        .timestamp(LocalDateTime.now())
                                        .amount(1000.00)
                                        .account(originAccount)
                                        .type(TransactionTypeEnum.INGRESO)
                                        .build();
                                tr.save(transferTransactionIngreso);

                                Transaction transferTransactionPago = Transaction.builder()
                                        .CbuOrigen(originAccount.getCbu())
                                        .CbuDestino(destinationAccount.getCbu())
                                        .description("Transferencia entre cuentas USD")
                                        .timestamp(LocalDateTime.now())
                                        .amount(1000.00)
                                        .account(destinationAccount)
                                        .type(TransactionTypeEnum.PAGO)
                                        .build();
                                tr.save(transferTransactionPago);

                                transferCount++;  // Incrementa el contador de transferencias
                            } else {
                                System.out.println("No se encontró una cuenta destino válida para el usuario: " +
                                        originAccount.getUser().getFirstName());
                            }
                        }
                    }




                }
            };
        }
    }


