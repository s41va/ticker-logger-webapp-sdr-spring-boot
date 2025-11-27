package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos;


import jakarta.validation.constraints.NotEmpty;
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
public class UsersDTO {

    private Long id;

    @NotEmpty(message = "{msg.user.username.notEmpty}")
    @Size(max = 40, message = "{msg.user.username.size}")
    private String username;

    @NotEmpty(message = "{msg.user.passwordHash.notEmpty}")
    @Size(max = 500, message = "{msg.user.passwordHash.size}")
    private String passwordHash;


    private boolean active;
    private boolean accountNonLocked;
    private LocalDateTime lastPasswordChange;
    private LocalDateTime passwordExpiresAt;
    private int failedLoginAttempts;
    private boolean emailVerified;
    private boolean mustChangePassword;
}
