package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.UserDTO;
import com.example.bbva.squad2.Wallet.dtos.RecipientResponseDTO;
import com.example.bbva.squad2.Wallet.dtos.RecipientDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.RoleName;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.services.UserService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UsuarioLoggeadoService usuarioLoggeadoService;

    private UsuarioSeguridad mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UsuarioSeguridad();
        mockUser.setId(1L);
        mockUser.setRole("ADMIN");
    }

    @Test
    void shouldReturnUserListForAdmin() {
        // Arrange
        Role rolAdmin = new Role(RoleName.ADMIN, "ADMIN", LocalDateTime.now(), LocalDateTime.now());
        UserDTO user1 = new UserDTO(1L, "John", "Doe", "john.doe@example.com", rolAdmin, null);
        UserDTO user2 = new UserDTO(2L, "Jane", "Smith", "jane.smith@example.com", rolAdmin, null);
        List<UserDTO> userList = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(userList);

        // Act
        List<UserDTO> result = userController.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        // Arrange
        Long userId = 1L;

        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(mockUser);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId, null);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithoutAdminRole() {
        // Arrange
        Long userId = 1L;
        mockUser.setRole("USER");

        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(mockUser);

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> userController.deleteUser(userId, null));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Usted no esta autorizado para eliminar usuarios.", exception.getMessage());
    }

    @Test
    void shouldAddBeneficiarySuccessfully() {
        // Arrange
        RecipientDTO recipientDTO = new RecipientDTO("1234567890123456789012");
        RecipientResponseDTO responseDTO = new RecipientResponseDTO();
        responseDTO.setIdRecipient(2L);
        responseDTO.setNombreApellido("Jane Smith");
        responseDTO.setUsername("jane.smith@example.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(mockUser);
        when(userService.addBeneficiario(mockUser.getId(), recipientDTO)).thenReturn(ResponseEntity.ok(responseDTO));

        // Act
        ResponseEntity<RecipientResponseDTO> response = userController.addBeneficiario(null, recipientDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void shouldGetBeneficiariesSuccessfully() {
        // Arrange
        RecipientResponseDTO beneficiary1 = new RecipientResponseDTO();
        beneficiary1.setIdRecipient(1L);
        beneficiary1.setNombreApellido("John Doe");

        RecipientResponseDTO beneficiary2 = new RecipientResponseDTO();
        beneficiary2.setIdRecipient(2L);
        beneficiary2.setNombreApellido("Jane Smith");

        List<RecipientResponseDTO> beneficiaries = List.of(beneficiary1, beneficiary2);

        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(mockUser);
        when(userService.getBeneficiarios(mockUser.getId())).thenReturn(beneficiaries);

        // Act
        ResponseEntity<List<RecipientResponseDTO>> response = userController.getBeneficiarios(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(beneficiary1, response.getBody().get(0));
        assertEquals(beneficiary2, response.getBody().get(1));
    }
}
