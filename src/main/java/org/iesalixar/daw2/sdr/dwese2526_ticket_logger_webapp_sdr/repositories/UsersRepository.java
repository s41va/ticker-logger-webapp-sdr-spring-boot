package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories;


import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
    @Override
    Optional<User> findById(Long Id);

}
