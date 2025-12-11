package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDetailDTO {

    private Long id;
    private String email;
    private String passwordHash;
    private boolean active;
    private boolean accountNonLocked;
    private LocalDateTime lastPasswordChange;
    private LocalDateTime passwordExpiresAt;
    private Integer failedLoginAttempts;
    private boolean emailVerified;
    private boolean mustChangePassword;


    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImage;
    private String bio;
    private String locale;
    private Set<String> roles;

}
