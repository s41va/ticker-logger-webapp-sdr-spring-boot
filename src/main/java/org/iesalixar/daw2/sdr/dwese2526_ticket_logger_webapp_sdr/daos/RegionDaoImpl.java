package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class RegionDaoImpl implements RegionDAO{

    private static final Logger logger = LoggerFactory.getLogger(RegionDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Region> listAllRegions()  {
        logger.info("Entrando en el metodo listAllRegions");
        String hql = "SELECT r FROM Region r";
        List<Region> regions = entityManager.createQuery(hql,Region.class).getResultList();
        logger.info("Retrieved {} regions from the database", regions.size());
        return regions;
    }

    @Override
    public List<Region> listAllRegionsPage(int page, int size, String sortField, String sortDir) {
        logger.info("Escuchando regions page={} - size={} - sortField={} - sortDir={} de la base de datos",
                page, size, sortField, sortDir);
        int offset = page * size;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Region> cq = cb.createQuery(Region.class);
        Root<Region> root = cq.from(Region.class);
        // 2. Determinar el campo de ordenación permitido (whitelist)
        Path<?> sortPath;
        switch (sortField) {
            case "id" -> sortPath = root.get("id");
            case "code" -> sortPath = root.get("code");
            case "name" -> sortPath = root.get("name");
            default -> {
                logger.warn("Unknown sortField '{}', defaulting to 'name'.", sortField);
                sortPath = root.get("name");
            }
        }
        // 3. Dirección de ordenación
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        // cb.desc y cb.asc son funciones predefinidas de criteria para las ordenaciones
        Order order = descending ? cb.desc(sortPath) : cb.asc(sortPath);
        // 4. Aplicar ordenación a la query
        cq.select(root).orderBy(order);
         // 5. Crear TypedQuery, aplicar paginación y ejecutar
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countRegion() {
        String hql = "SELECT count(r) FROM Region r";
        Long total = entityManager.createQuery(hql, Long.class).getSingleResult();
        return (total != null) ? total : 0L;
    }


    @Override
    public void insertRegion(Region region)  {
        logger.info("Insertando region con codigo: {} y nombre: {}", region.getCode(), region.getName() );
        entityManager.persist(region);
        logger.info("Inserted region succesfull");


    }


    @Override
    public boolean existsRegionByCode(String code) {
        logger.info("Entrando en el metodo existsRegionByCode");
        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region con codigo: {} existe: {}", code, exists);
        return exists;
    }


    @Override
    public void updateRegion(Region region)  {
        logger.info("Updating region with id: {}", region.getId());
        entityManager.merge(region);
        logger.info("Updated region.");
    }




    @Override
    public Region getRegionById(Long id) {
        logger.info("Entrando en el metodo getRegionbyId");
        Region region = entityManager.find(Region.class, id);
        if (region!=null){
            logger.info("Region obtenida {} - {}", region.getCode(), region.getName());
        }else{
            logger.warn("No se ha encontrado ninguna region con id: {}", id);
        }
        return region;

    }


    @Override
    public boolean existsRegionByCodeAndNotId(String code, Long id) {
        logger.info("Entrando al metodo existsRegionByCodeAndNotId");
        String hql = "SELECT COUNT(r) FROM Region r WHERE UPPER(r.code) = :code AND r.id != :id";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Region with code: {} exists excluding id: {}: {}", code, id, exists);
        return exists;
    }


    @Override
    public void deleteRegion(Long id) {
        logger.info("Entrando al metodo deleteRegion");
        Region region = entityManager.find(Region.class, id);
        if (region!=null){
            entityManager.remove(region);
            logger.info("Region con id: {} eliminada", id);
        }else {
            logger.warn("Region con id: {} no existe", id);
        }
    }
}





















