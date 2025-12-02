package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersUpdateDTO {

    @NotNull(message = "{msg.user.id.notNull}")
    private Long id;


    @NotBlank(message = "{msg.user.username.notEmpty}")
    @Size(max = 40, message = "{msg.user.username.size}")
    private String username;


    @NotBlank(message = "{msg.user.passwordHash.notEmpty")
    @Size(max = 40, message = "{msg.user.passwordHash.size}")
    private String passwordHash;


    @NotNull(message = "{msg.user.active.notNull}")
    private Boolean active; // Usamos Boolean objeto para permitir la validación @NotNull

    @NotNull(message = "{msg.user.accountNonLocked.notNull}")
    private Boolean accountNonLocked;

    @NotNull(message = "{msg.user.lastPasswordChange.notNull}")
    private LocalDateTime lastPasswordChange;

    private LocalDateTime passwordExpiresAt;

    private Integer failedLoginAttempts;

    @NotNull(message = "{msg.user.emailVerified.notNull}")
    private Boolean emailVerified;

    @NotNull(message = "{msg.user.mustChangePassword.notNull}")
    private Boolean mustChangePassword;
}
