package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;


import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Users;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define las operaciones de acceso a datos (DAO) para la entidad {@link Users}.
 * <p>
 * Proporciona los métodos necesarios para realizar operaciones CRUD
 * (crear, leer, actualizar y eliminar) sobre la tabla de usuarios en la base de datos.
 * </p>
 *
 * <p>Las implementaciones de esta interfaz deben encargarse de manejar la lógica
 * de conexión y las consultas SQL necesarias.</p>
 *
 * <p>Ejemplo de implementación esperada:</p>
 * <pre>
 *     public class UsersDAOImpl implements UsersDAO {
 *         // Implementación de los métodos definidos en la interfaz
 *     }
 * </pre>
 *
 * @author Salvador Diaz Román
 * @version 1.0
 */
public interface UsersDAO {

    /**
     * Obtiene una lista con todos los usuarios almacenados en la base de datos.
     *
     * @return una lista de objetos {@link Users} representando todos los usuarios registrados.
     * @throws SQLException si ocurre un error al realizar la consulta.
     */
    List<Users> listAllUsers() ;

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param user el objeto {@link Users} que contiene la información del nuevo usuario a registrar.
     * @throws SQLException si ocurre un error durante la inserción.
     */
    void insertUser(Users user) ;

    /**
     * Verifica si existe un usuario con un nombre de usuario específico.
     *
     * @param username el nombre de usuario que se desea comprobar.
     * @return {@code true} si el usuario existe, {@code false} en caso contrario.
     * @throws SQLException si ocurre un error al ejecutar la consulta.
     */
    boolean existsUserByUsername(String username) ;

    /**
     * Obtiene la información de un usuario a partir de su identificador único.
     *
     * @param id el identificador único del usuario.
     * @return un objeto {@link Users} con la información del usuario, o {@code null} si no se encuentra.
     * @throws SQLException si ocurre un error al realizar la consulta.
     */
    Users getUsersById(long id) ;

    /**
     * Elimina un usuario de la base de datos a partir de su identificador.
     *
     * @param id el identificador único del usuario que se desea eliminar.
     * @throws SQLException si ocurre un error al realizar la eliminación.
     */
    void deleteUsers(long id) ;

    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param updated el objeto {@link Users} que contiene los nuevos datos del usuario.
     * @throws SQLException si ocurre un error durante la actualización.
     */
    void updateUsers(Users updated) ;

    /**
     * Verifica si existe un usuario con un nombre de usuario específico,
     * excluyendo un identificador determinado (útil para validaciones durante una actualización).
     *
     * @param username el nombre de usuario que se desea comprobar.
     * @param id el identificador del usuario que debe ser excluido de la comprobación.
     * @return {@code true} si existe otro usuario con el mismo nombre de usuario, {@code false} en caso contrario.
     * @throws SQLException si ocurre un error al ejecutar la consulta.
     */
    boolean existsUserByUsernameAndNotId(String username, long id) ;
}
