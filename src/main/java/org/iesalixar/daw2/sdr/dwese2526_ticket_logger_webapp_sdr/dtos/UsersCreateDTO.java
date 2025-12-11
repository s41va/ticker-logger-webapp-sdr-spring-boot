package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersCreateDTO {

    @NotNull(message = "{msg.user.id.notNull}")
    private Long id;

    @NotBlank(message = "{msg.user.email.notEmpty}")
    @Size(max = 40, message = "{msg.user.email.size}") // Usando el constraint (varchar 40)
    private String email;

    // 🔒 Password Hash (solo relevante para la entidad/almacenamiento)
    // Este campo no suele validarse directamente en los DTOs de entrada,
    // ya que la contraseña sin hash (rawPassword) es la que se valida primero.
    // Si se mapeara, sería para asegurar el tamaño:
    // @NotBlank(message = "{msg.user.passwordHash.notEmpty}")
    // @Size(max = 500, message = "{msg.user.passwordHash.size}") // Usando el constraint (varchar 500)
    @NotBlank(message = "{msg.user.passwordHash.notEmpty}")
    @Size(max = 100, message = "{msg.user.passwordHash.size}")
    private String passwordHash;

    // 🟢 Estado de la cuenta (los booleanos se validan con @NotNull si es obligatorio en un DTO)
    @NotNull(message = "{msg.user.active.notNull}")
    private boolean active;

    @NotNull(message = "{msg.user.accountNonLocked.notNull}")
    private boolean accountNonLocked;

    @NotNull(message = "{msg.user.emailVerified.notNull}")
    private boolean emailVerified;

    @NotNull(message = "{msg.user.mustChangePassword.notNull}")
    private boolean mustChangePassword;

    // 🕰️ Fechas (se validan con @NotNull)
    @NotNull(message = "{msg.user.lastPasswordChange.notNull}")
    private LocalDateTime lastPasswordChange;

    @NotNull(message = "{msg.user.passwordExpiresAt.notNull}")
    private LocalDateTime passwordExpiresAt;

    // 🔢 Intentos de login fallidos (se validan con @NotNull y quizás un rango)
    @NotNull(message = "{msg.user.failedLoginAttempts.notNull}")
    @Min(value = 0, message = "{msg.user.failedLoginAttempts.min}")
    private Integer failedLoginAttempts;

    @NotEmpty(message = "{msg.user.roles.notEmpty}")
    private Set<Long> roleIds = new HashSet<>();
}
