package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;

import java.util.List;

public class UsersMapper {

    /**
     * Convierte una entidad {@link User} a {@link UserDTO} (vista simple).
     * Incluye campos de estado de la cuenta relevantes para una vista de lista.
     */
    public static UsersDTO toDTO(User entity){
        if (entity == null) return null;
        UsersDTO dto = new UsersDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        // Se omiten la contraseña (passwordHash) y campos de tiempo muy específicos para una vista simple.
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setActive(entity.isActive()); // Se incluye el estado de actividad
        dto.setAccountNonLocked(entity.isAccountNonLocked()); // Se incluye el estado de bloqueo
        dto.setEmailVerified(entity.isEmailVerified()); // Se incluye el estado de verificación
        dto.setMustChangePassword(entity.isMustChangePassword());
        return dto;
    }

    /**
     * Convierte una lista de entidades {@link User} a {@link UsersDTO}.
     */
    public static List<UsersDTO> toDTOList(List<User> entities){
        if (entities == null) return List.of();
        return entities.stream().map(UsersMapper::toDTO).toList();
    }

    //
    // Entity -> DTO (detalle con todos los campos de estado y seguridad)
    //
    /**
     * Convierte una {@link User} a {@link UsersDetailDTO}, mapeando todos sus campos de seguridad y estado (incluyendo roles).
     */
    public static UsersDetailDTO toDetailDTO(User entity) {
        if (entity == null) return null;

        UsersDetailDTO dto = new UsersDetailDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());

        // Mapeo de campos de seguridad y estado
        dto.setActive(entity.isActive());
        dto.setAccountNonLocked(entity.isAccountNonLocked());
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        dto.setEmailVerified(entity.isEmailVerified());
        dto.setMustChangePassword(entity.isMustChangePassword());

        // Asume que el campo 'roles' existe en la entidad User
        // dto.setRoles(toRoleList(entity.getRoles()));
        return dto;
    }

    //
    // DTO -> Entity (Creación)
    //
    /**
     * Convierte un DTO de creación {@link UsersCreateDTO} a la entidad {@link User}.
     * Solo mapea los campos que el usuario proporciona inicialmente (username y quizás la contraseña temporal).
     */
    public static User toEntity(UsersCreateDTO dto){
        if (dto == null) return null;
        User e = new User();
        e.setUsername(dto.getUsername());

        // Los campos de seguridad/estado se suelen inicializar en el servicio o constructor:
        e.setPasswordHash(dto.getPasswordHash()); // Se manejaría en el servicio
        e.setActive(true); // El usuario está activo por defecto
        e.setAccountNonLocked(true); // No bloqueado por defecto
        e.setLastPasswordChange(dto.getLastPasswordChange());
        e.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        e.setFailedLoginAttempts(dto.getFailedLoginAttempts()); // Intentos a cero
        e.setEmailVerified(false); // Pendiente de verificación
        e.setMustChangePassword(true); // Se fuerza el cambio si se genera una contraseña temporal
        // Las fechas de cambio/expiración se establecen en el servicio

        return e;
    }

    //
    // Entity -> UpdateDTO
    //
    /**
     * Convierte una entidad {@link User} a {@link UsersUpdateDTO}.
     * Este DTO es útil para recuperar el estado actual para una edición.
     */
    public static UsersUpdateDTO toUpdateDTO(User entity) {
        if (entity == null) return null;
        UsersUpdateDTO dto = new UsersUpdateDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());

        // Campos de estado que un administrador podría querer actualizar
        dto.setLastPasswordChange(entity.getLastPasswordChange());
        dto.setPasswordExpiresAt(entity.getPasswordExpiresAt());
        dto.setActive(entity.isActive());
        dto.setAccountNonLocked(entity.isAccountNonLocked());
        dto.setEmailVerified(entity.isEmailVerified());
        dto.setMustChangePassword(entity.isMustChangePassword());

        return dto;
    }

    //
    // DTO -> Entity (Copia a entidad existente)
    //
    /**
     * Copia las propiedades de un DTO de actualización {@link UsersUpdateDTO} a una entidad {@link User} **existente**.
     */
    public static void copyToExistingEntity(UsersUpdateDTO dto, User entity){
        if (dto == null || entity == null) return;

        // El nombre de usuario puede ser editable o no, depende de la lógica de negocio
        entity.setUsername(dto.getUsername());

        // Campos de estado que se pueden actualizar desde el DTO
        entity.setActive(dto.getActive());
        entity.setAccountNonLocked(dto.getAccountNonLocked());
        entity.setEmailVerified(dto.getEmailVerified());
        entity.setMustChangePassword(dto.getMustChangePassword());

        if (dto.getFailedLoginAttempts() != null ) entity.setFailedLoginAttempts(dto.getFailedLoginAttempts());
        if (dto.getLastPasswordChange() != null) entity.setLastPasswordChange(dto.getLastPasswordChange());
        if (dto.getPasswordExpiresAt() != null ) entity.setPasswordExpiresAt(dto.getPasswordExpiresAt());
        // NOTA: Los campos failedLoginAttempts, passwordHash y las fechas
        // NO deberían mapearse directamente, sino ser gestionados en la capa de Servicio.
        // Ej: Si accountNonLocked cambia a true, el Servicio reiniciaría failedLoginAttempts.
    }

    // Si la entidad tiene Roles, necesitarías estas funciones auxiliares (como en el ejemplo de Region):
    /*
    public static RoleDTO toRoleDTO(Role r) { ... }
    public static List<RoleDTO> toRoleList(List<Role> roles) { ... }
    */
}