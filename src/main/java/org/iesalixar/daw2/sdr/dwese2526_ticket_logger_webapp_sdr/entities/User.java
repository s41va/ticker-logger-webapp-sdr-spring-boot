package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.persistence.*;
import lombok.*;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 40)
    private String email;


    // Constraints para passwordHash (varchar 500)

    @Column(name="password_hash", nullable = false, length = 500)
    private String passwordHash;

    @Column(name="active", nullable = false)
    private boolean active = Boolean.TRUE;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = Boolean.TRUE;

    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;

    @Column(name = "password_expires_at")
    private LocalDateTime passwordExpiresAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = Boolean.FALSE;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword = Boolean.FALSE;


    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserProfile profile;


    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();


//    @Override
//    public int hashCode() {
//        // Replace: return userProfile.hashCode();  <-- This causes the recursion
//
//        // With: return id != null ? id.hashCode() : 0;
//
//        // OR, if using Lombok/IDE generated methods, exclude the UserProfile field:
//        return Objects.hash(email, passwordHash, active, accountNonLocked, lastPasswordChange, failedLoginAttempts, emailVerified, mustChangePassword, roles); // Exclude the UserProfile object
//    }

    /**
     *
     * @param email (varchar 40) → identificador único de login.
     * @param passwordHash (varchar 500)→ contraseña de momento en texto plano.
     * @param active (Boolean)→ indica si la cuenta está activa o bloqueada.
     * @param accountNonLocked (Boolean)→ campo para saber si una cuenta está bloqueada por intentos fallidos.
     * @param lastPasswordChange (LocalDateTime) → fecha del último cambio de contraseña → sirve para comprobar si han pasado más de 3 meses.
     * @param passwordExpiresAt (LocalDateTime) → fecha exacta de caducidad (calculada a partir de lastPasswordChange), es decir, tres meses posterior a lastPasswordChange.
     * @param failedLoginAttempts (Integer) → número de logins con intentos fallidos.
     * @param emailVerified (Boolean) → si el correo fue validado.
     * @param mustChangePassword (Boolean) → fuerza a cambiar la contraseña en el próximo login.
     */
    public User(String email, String passwordHash, boolean active, boolean accountNonLocked,
                LocalDateTime lastPasswordChange, LocalDateTime passwordExpiresAt, int failedLoginAttempts, boolean emailVerified, boolean mustChangePassword) {
        this.email = email;
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