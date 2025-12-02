package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Constraints para username (varchar 40)

    @Column(name="username", nullable = false)
    private String username;

    // Constraints para passwordHash (varchar 500)

    @Column(name="password_hash", nullable = false)
    private String passwordHash;

    @Column(name="active", nullable = false)
    private boolean active;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;

    @Column(name = "last_password_change", nullable = false)
    private LocalDateTime lastPasswordChange;

    @Column(name = "password_expires_at", nullable = false)
    private LocalDateTime passwordExpiresAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "must_change_password", nullable = false)
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
    public User(String username, String passwordHash, boolean active, boolean accountNonLocked,
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