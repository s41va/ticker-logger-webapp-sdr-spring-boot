package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> listAllRoles();
    List<Role> findAllByIds(Set<Long> ids);
}
