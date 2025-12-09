package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Role;

import java.util.List;
import java.util.Set;

public interface RoleDAO {
    List<Role> listAllRoles();
    List<Role> findAllByIds(Set<Long> ids);
}
