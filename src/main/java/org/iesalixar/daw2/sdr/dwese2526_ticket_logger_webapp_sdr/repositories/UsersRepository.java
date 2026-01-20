package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories;


import jakarta.persistence.Entity;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos (DAO) para la entidad {@link User}.
 * <p>
 * Proporciona los métodos necesarios para realizar operaciones CRUD
 * (crear, leer, actualizar y eliminar) sobre la tabla de usuarios en la base de datos.
 * </p>
 *
 * @author Salvador Diaz Román
 * @version 2.0
 */
public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email) ;
    boolean existsByEmailAndIdNot(String email, Long id);
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String Email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);


}
