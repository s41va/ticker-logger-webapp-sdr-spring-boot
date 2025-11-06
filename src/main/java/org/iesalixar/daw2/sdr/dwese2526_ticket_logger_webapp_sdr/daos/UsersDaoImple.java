package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 *  Implementación de la interfaz {@link UsersDAO} que gestiona las operaciones CRUD
 * sobre la tabla <code>users</code> de la base de datos, utilizando **Spring JdbcTemplate**.
 *
 * <p>Esta clase utiliza {@link JdbcTemplate} para la interacción con la base de datos.</p>
 *
 * @author Salvador Diaz Roman
 * @version 2.0 (Adaptado a JdbcTemplate)
 */
@Repository
public class UsersDaoImple implements UsersDAO {

    private static final Logger logger = LoggerFactory.getLogger(UsersDaoImple.class);

    private final JdbcTemplate jdbcTemplate;

    /**
     * 🛠️ Constructor que inyecta la dependencia de JdbcTemplate.
     * @param jdbcTemplate el objeto JdbcTemplate proporcionado por Spring.
     */
    public UsersDaoImple(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en la base de datos.
     *
     * @return una lista de objetos {@link Users} que representan todos los usuarios
     */
    @Override
    public List<Users> listAllUsers() {
        logger.info("Entrando en el metodo listAllUsers");
        String sql = "SELECT * FROM users";
        // Usa BeanPropertyRowMapper para mapear automáticamente las columnas a los campos de la clase Users
        List<Users> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Users.class));
        logger.info("Retrieved {} users from the database", users.size());
        return users;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param user el objeto {@link Users} que contiene los datos del nuevo usuario
     */
    @Override
    public void insertUser(Users user) {
        logger.info("Insertando usuario: {}", user.getUsername());
        String sql = """
                INSERT INTO users (username, passwordHash, active,
                accountNonLocked, lastPasswordChange, passwordExpiresAt,
                failedLoginAttempts, emailVerified, mustChangePassword)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;

        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPasswordHash(),
                user.isActive(),
                user.isAccountNonLocked(),
                user.getLastPasswordChange() != null ? Timestamp.valueOf(user.getLastPasswordChange()) : null,
                user.getPasswordExpiresAt() != null ? Timestamp.valueOf(user.getPasswordExpiresAt()) : null,
                user.getFailedLoginAttempts(),
                user.isEmailVerified(),
                user.isMustChangePassword());

        logger.info("Inserted user. Rows affected: {}", rowsAffected);
    }

    /**
     * Verifica si existe un usuario con un nombre de usuario determinado.
     *
     * @param username el nombre de usuario a buscar (no sensible a mayúsculas)
     * @return {@code true} si el usuario existe, {@code false} en caso contrario
     */
    @Override
    public boolean existsUserByUsername(String username) {
        logger.info("Entrando en el metodo existsUserByUsername para: {}", username);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ?";

        // queryForObject(sql, requiredType, args) simplifica la cuenta
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username.toUpperCase());
        boolean exists = count != null && count > 0;

        logger.info("User con username: {} existe: {}", username, exists);
        return exists;
    }

    /**
     * Recupera un usuario por su identificador único (ID).
     *
     * @param id el identificador del usuario a buscar
     * @return el objeto {@link Users} correspondiente al ID, o {@code null} si no existe
     */
    @Override
    public Users getUsersById(long id) {
        logger.info("Entrando en el metodo getUsersById para ID: {}", id);
        String sql = "SELECT * FROM users WHERE id=?";

        try {
            // queryForObject puede lanzar excepciones si no encuentra resultados
            Users user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Users.class), id);
            logger.info(" Usuario encontrado con ID: {}", id);
            return user;
        } catch (Exception e) {
            logger.warn("Usuario no encontrado con el siguiente id: {}", id);
            return null;
        }
    }

    /**
     * Elimina un usuario de la base de datos según su identificador.
     *
     * @param id el identificador del usuario a eliminar
     */
    @Override
    public void deleteUsers(long id) {
        logger.info("Entrando al metodo deleteUsers para ID: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info(" Deleted user with ID: {}. Rows affected: {}", id, rowsAffected);
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     *
     * @param user el objeto {@link Users} con los datos actualizados
     */
    @Override
    public void updateUsers(Users user) {
        logger.info(" Updating user with id: {}", user.getId());
        String sql = """
                UPDATE users SET
                    username = ?,
                    passwordHash = ?,
                    active = ?,
                    accountNonLocked = ?,
                    lastPasswordChange = ?,
                    passwordExpiresAt = ?,
                    failedLoginAttempts = ?,
                    emailVerified = ?,
                    mustChangePassword = ?
                WHERE id = ?
                """;

        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPasswordHash(),
                user.isActive(),
                user.isAccountNonLocked(),
                user.getLastPasswordChange() != null ? Timestamp.valueOf(user.getLastPasswordChange()) : null,
                user.getPasswordExpiresAt() != null ? Timestamp.valueOf(user.getPasswordExpiresAt()) : null,
                user.getFailedLoginAttempts(),
                user.isEmailVerified(),
                user.isMustChangePassword(),
                user.getId());

        logger.info(" Updated user. Rows affected: {}", rowsAffected);
    }

    /**
     * Comprueba si existe un usuario con un nombre de usuario dado, excluyendo un ID concreto.
     * <p>Ahora implementado con JdbcTemplate.</p>
     *
     * @param username el nombre de usuario a buscar
     * @param id el ID del usuario que se debe excluir de la búsqueda
     * @return {@code true} si existe otro usuario con ese nombre, {@code false} en caso contrario
     */
    @Override
    public boolean existsUserByUsernameAndNotId(String username, long id) {
        logger.info(" Entrando al metodo existsUserByUsernameAndNotId para username: {} excluyendo ID: {}", username, id);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        logger.info(" User with username: {} exists excluding id: {}: {}", username, id, exists);
        return exists;
    }
}