package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Users {

    private Long id;

    // Constraints para username (varchar 40)
    @NotEmpty(message = "{msg.user.username.notEmpty}")
    @Size(max = 40, message = "{msg.user.username.size}")
    private String username;

    // Constraints para passwordHash (varchar 500)
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

    /**
     *
     * @param username (varchar 40) → identificador único de login.
     * @param passwordHash (varchar 500)→ contraseña de momento en texto plano.
     * @param active (Boolean)→ indica si la cuenta está activa o bloqueada.
     * @param accountNonLocked (Boolean)→ campo para saber si una cuenta está bloqueada por intentos fallidos.
     * @param lastPasswordChange (LocalDateTime) → fecha del último cambio de contraseña → sirve para comprobar si han pasado más de 3 meses.
     * @param passwordExpiresAt (LocalDateTime) → fecha exacta de caducidad (calculada a partir de lastPasswordChange), es decir, tres meses posterior a lastPasswordChange.
     * @param failedLoginAttempts (Integer) → número de logins con intentos fallidos.
     * @param emailVerified (Boolean) → si el correo fue validado.
     * @param mustChangePassword (Boolean) → fuerza a cambiar la contraseña en el próximo login.
     */
    public Users( String username, String passwordHash, boolean active, boolean accountNonLocked,
                  LocalDateTime lastPasswordChange, LocalDateTime passwordExpiresAt, int failedLoginAttempts, boolean emailVerified, boolean mustChangePassword) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.active = active;
        this.accountNonLocked = accountNonLocked;
        this.lastPasswordChange = lastPasswordChange;
        this.passwordExpiresAt = passwordExpiresAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.emailVerified = emailVerified;
        this.mustChangePassword = mustChangePassword;
    }
}