package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegionDaoImpl implements RegionDAO{

    private static final Logger logger = LoggerFactory.getLogger(RegionDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public RegionDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Region> listAllRegions()  {
        logger.info("Entrando en el metodo listAllRegions");
        String sql = "SELECT * FROM regions";
        List<Region> regions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Region.class));
        logger.info("Retrieved {} regions from the database", regions.size());
        return regions;
    }


    @Override
    public void insertRegion(Region region)  {
        logger.info("Insertando region");
        String sql = "INSERT INTO regions (code, name) VALUES (?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, region.getCode(), region.getName());
        logger.info("Inserted region. Rows affected: {}", rowsAffected);


    }


    @Override
    public boolean existsRegionByCode(String code) {
        logger.info("Entrando en el metodo existsRegionByCode");
        String sql = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code.toUpperCase());
        boolean exists = count != null && count > 0;
        logger.info("Region con codigo: {} existe: {}", code, exists);
        return exists;
    }


    @Override
    public void updateRegion(Region region)  {
        logger.info("Updating region with id: {}", region.getId());
        String sql = "UPDATE regions SET code = ?, name = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, region.getCode(), region.getName(), region.getId());
        logger.info("Updated region. Rows affected: {}", rowsAffected);
    }




    @Override
    public Region getRegionById(Long id) {
        logger.info("Entrando en el metodo getRegionbyId");

        String sql = "SELECT * FROM regions WHERE id = ?";
        try {
            Region region = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Region.class), id);
            logger.info("Saliendo del metodo getRegionbyId {} - {}" , region.getCode(), region.getId());
            return region;
        } catch (Exception e) {
            logger.warn("Region no encontrado con el siguiente id: {}", id);
            return null;
        }

    }


    @Override
    public boolean existsRegionByCodeAndNotId(String code, Long id) {
        logger.info("Entrando al metodo existsRegionByCodeAndNotId");
        String sql = "SELECT COUNT(*) FROM regions WHERE UPPER(code) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        logger.info("Region with code: {} exists excluding id: {}: {}", code, id, exists);
        return exists;
    }


    @Override
    public void deleteRegion(Long id) {
        logger.info("Entrando al metodo deleteRegion");
        String sql = "DELETE FROM regions WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleted region. Rows affected: {}", rowsAffected);


    }
}





















