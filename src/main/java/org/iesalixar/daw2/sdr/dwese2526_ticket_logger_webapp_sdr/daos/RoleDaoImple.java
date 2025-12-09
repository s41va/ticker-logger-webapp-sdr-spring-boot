package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class RoleDaoImple implements RoleDAO {

    private static final Logger logger = LoggerFactory.getLogger(RoleDaoImple.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista todos los roles de la base de datos.
     *
     * @return lista de roles
     */
    public List<Role> listAllRoles() {
        logger.info("Listing all roles from the database.");
        String hql = "SELECT r FROM Role r ORDER BY r.name";
        List<Role> roles = entityManager.createQuery(hql, Role.class).getResultList();
        logger.info("Retrieved {} roles from the database.", roles.size());
        return roles;
    }

    /**
     * Recupera todos los roles cuyos IDs estén en el conjunto proporcionado.
     *
     * @param ids conjunto de identificadores de rol
     * @return lista de roles que coinciden con los IDs dados.
     * Si el conjunto es null o está vacío, devuelve una lista vacía.
     */
    public List<Role> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            logger.info("findAllByIds called with null or empty ids. Returning empty list.");
            return List.of();
        }

        logger.info("Finding roles by ids: {}", ids);
        String hql = "SELECT r FROM Role r WHERE r.id IN :ids";
        List<Role> roles = entityManager.createQuery(hql, Role.class)
                .setParameter("ids", ids)
                .getResultList();
        logger.info("Found {} roles matching the given ids.", roles.size());
        return roles;
    }
}
