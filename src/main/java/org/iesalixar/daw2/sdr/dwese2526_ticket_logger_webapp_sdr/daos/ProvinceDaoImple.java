package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class ProvinceDaoImple implements ProvinceDAO{

    private static final Logger logger = LoggerFactory.getLogger(ProvinceDaoImple.class);

    @PersistenceContext
    private EntityManager entityManager;



    @Override
    public List<Province> listAllProvinces() {
        logger.info("Entrando al metodo listAllProvinced");
        String hql = "SELECT p FROM Province p JOIN FETCH p.region";
        List<Province> provinces = entityManager.createQuery(hql, Province.class).getResultList();
        logger.info("Retrieved {} provinces from the database. ", provinces.size());
        return provinces;
    }

    @Override
    public List<Province> listProvincesPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listando Provincias page={}, size={}, sortField={}, sortDir={} desde de la base de datos", page, size, sortField, sortDir);
        int offset = page * size;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Province> cq = cb.createQuery(Province.class);
        Root<Province> root = cq.from(Province.class);
        root.fetch("region", JoinType.INNER);
        Join<Province, Region> regionJoin = root.join("region", JoinType.INNER);
        // 2. Determinar el campo de ordenación permitido (whitelist)
        Path<?> sortPath;
        switch (sortField) {
            case "id" -> sortPath = root.get("id");
            case "code" -> sortPath = root.get("code");
            case "name" -> sortPath = root.get("name");
            case "regionName" -> sortPath = regionJoin.get("name");
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
    public long countProvince() {
        String hql = "SELECT COUNT(p) FROM Province p";
        Long total = entityManager.createQuery(hql, Long.class).getSingleResult();
        return (total != null) ? total : 0L;
    }

    /**
     * Verifica si existe una provincia con el código especificado.
     * @param code código de la provincia a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsProvinceByCode(String code) {
        logger.info("Checking if province with code: {} exists", code);

        String hql = "SELECT COUNT(p) FROM Province p WHERE UPPER(p.code) = :code";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .getSingleResult();

        boolean exists = count != null && count > 0;

        logger.info("Province with code: {} exists: {}", code, exists);

        return exists;
    }

    @Override
    public boolean existsProvinceByCodeAndNotId(String code, Long id) {
        logger.info("Verificando si existe la provincia con el codigo: {} e id: {}", code, id);
        String hql = "SELECT COUNT(p) FROM Province p  WHERE UPPER(p.code)= :code AND p.id != :id";
        Long count = entityManager.createQuery(hql, Long.class )
                .setParameter("code", code)
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count!=null && count >0 ;
        logger.info("Provincia con codigo: {} existe con id: {}: {}", code, id, exists);
        return exists;
    }


    @Override
    public void insertProvince(Province province) {
        logger.info("Inserting province with code: {}, name {}, region{}",
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null);

        entityManager.persist(province);

        logger.info("Inserted province con id: {}", province.getId() );
    }

    @Override
    public void updateProvince(Province province) {
        logger.info("Actualizando provincia con id: {}", province.getId());
        entityManager.merge(province);
        logger.info("Actualizado provincias. Lines afectadas: {}",province.getId());
    }

    @Override
    public Province getProvinceById(Long id) {
        logger.info("Retrieving province by id: {}", id);
        Province province = entityManager.find(Province.class, id);
        if (province!=null){
            logger.info("Provincia encontrada con id: {} - {}",province.getId(), province.getName());
        }else{
            logger.warn("Provincia con id: {} no encontrada", province.getId());
        }
        return province;
    }

    @Override
    public void deleteProvince(Long id){
        logger.error("Eliminando provincia con id: {}", id);
        Province province = entityManager.find(Province.class, id);
        if (province!=null){
            entityManager.remove(province);
            logger.info("Provincia con id: {} eliminada", id);
        }else{
            logger.warn("Provincia con id: {} no encontrada", id);
        }

    }

}
