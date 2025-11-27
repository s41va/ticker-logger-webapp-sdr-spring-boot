package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
@Transactional
@Repository
public class UsersDaoImple implements UsersDAO {

    private static final Logger logger = LoggerFactory.getLogger(UsersDaoImple.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Obtiene una lista de todos los usuarios registrados en la base de datos.
     *
     * @return una lista de objetos {@link User} que representan todos los usuarios
     */
    @Override
    public List<User> listAllUsers() {
        logger.info("Entrando en el metodo listAllUsers");
        String hql = "SELECT u FROM User u";
        // Usa BeanPropertyRowMapper para mapear automáticamente las columnas a los campos de la clase Users
        List<User> users = entityManager.createQuery(hql, User.class).getResultList();
        logger.info("Retrieved {} users from the database", users.size());
        return users;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param user el objeto {@link User} que contiene los datos del nuevo usuario
     */
    @Override
    public void insertUser(User user) {
        logger.info("Insertando usuario: {}", user.getUsername());
        entityManager.persist(user);
        logger.info("Inserted user successful");
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
        String hql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        boolean exists = count != null && count > 0;

        logger.info("User con username: {} existe: {}", username, exists);
        return exists;
    }

    /**
     * Recupera un usuario por su identificador único (ID).
     *
     * @param id el identificador del usuario a buscar
     * @return el objeto {@link User} correspondiente al ID, o {@code null} si no existe
     */
    @Override
    public User getUsersById(long id) {
        logger.info("Entrando en el metodo getUsersById para ID: {}", id);
        User users = entityManager.find(User.class, id);

        if (users != null){
            logger.info("Usuario encontrado: {} - {}", users.getId(), users.getUsername());
        }else {
            logger.warn("Ningun usuario encontrado con el id - {}", id);
        }
        return users;
    }

    /**
     * Elimina un usuario de la base de datos según su identificador.
     *
     * @param id el identificador del usuario a eliminar
     */
    @Override
    public void deleteUsers(long id) {
        logger.info("Entrando al metodo deleteUsers para ID: {}", id);
        User users = entityManager.find(User.class, id);
        if (users != null){
            entityManager.remove(users);
            logger.info("Usuario eliminado correctamente");
        }else{
            logger.warn("Region con el  id = {} no ha podido ser encontrado", id);
        }
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     *
     * @param user el objeto {@link User} con los datos actualizados
     */
    @Override
    public void updateUsers(User user) {
        logger.info(" Updating user with id: {}", user.getId());
        entityManager.merge(user);
        logger.info(" Updated user successful" );
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
        String hql = "SELECT COUNT(u) FROM User u WHERE UPPER(u.username) = :username AND u.id != :id";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("username", username.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info(" User with username: {} exists excluding id: {}: {}", username, id, exists);
        return exists;
    }
}