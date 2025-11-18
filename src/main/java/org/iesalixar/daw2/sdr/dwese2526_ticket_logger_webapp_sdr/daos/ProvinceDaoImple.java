package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Province;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProvinceDaoImple implements ProvinceDAO{

    private static final Logger logger = LoggerFactory.getLogger(ProvinceDaoImple.class);

    private final JdbcTemplate jdbcTemplate;

    public ProvinceDaoImple(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    private final RowMapper<Province> provinceRowMapper = (rs, rowNum) -> {
        // Creamos la instancia de Province que vamos a devolver
        Province province = new Province();

        // Mapeamos las columnas propias de la tabla provinces
        // // OJO: los alias usados en el SELECT deben coincidir con estos nombres de columna
        province.setId(rs.getLong("id"));
        province.setCode(rs.getString("code"));
        province.setName(rs.getString("name"));

        // Ahora mapeamos la región asociada (JOIN con regions)
        // // Usamos alias en el SELECT: r.id AS region_id, r.code AS region_code, r.name AS region_name
        Region region = new Region();
        region.setId(rs.getLong("region_id"));
        region.setCode(rs.getString("region_code"));
        region.setName(rs.getString("region_name"));

        // Asignamos el objeto Region a la Province
        province.setRegion(region);

        // Devolvemos la Province completamente montada
        return province;
    };


    @Override
    public List<Province> listAllProvinces() {
        logger.info("Entrando al metodo listAllProvinced");
        String sql =
                "SELECT p.id, p.code, p.name, r.id as region_id, r.code as region_code, r.name as region_name " +
                "FROM provinces p " +
                        "JOIN regions r ON p.region_id = r.id";

        List<Province> provinces = jdbcTemplate.query(sql, provinceRowMapper);
        logger.info("Retrieved {} provinces from the database. ", provinces.size());
        return provinces;
    }

    /**
     * Verifica si existe una provincia con el código especificado.
     * @param code código de la provincia a verificar.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsProvinceByCode(String code) {
        logger.info("Checking if province with code: {} exists", code);

        String sql = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code.toUpperCase());

        boolean exists = count != null && count > 0;

        logger.info("Province with code: {} exists: {}", code, exists);

        return exists;
    }

    @Override
    public boolean existsProvinceByCodeAndNotId(String code, Long id) {
        logger.info("Verificando si existe la provincia con el codigo: {} e id: {}", code, id);
        String sql = "SELECT COUNT(*) FROM provinces WHERE UPPER(code)=? AND id != ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code.toUpperCase(), id);
        boolean exists = count!=null && count >0 ;
        logger.info("Provincia con codifo: {} existe con id: {}: {}", code, id, exists);
        return exists;
    }


    @Override
    public void insertProvince(Province province) {
        logger.info("Inserting province with code: {}, name {}, region{}",
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null);

        String sql = "INSERT INTO provinces (code, name, region_id) VALUES (?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null);

        logger.info("Inserted province. Rows affected:  {}", rowsAffected);
    }

    @Override
    public void updateProvince(Province province) {
        logger.info("Actualizando provincia con id: {}", province.getId());
        String sql = "UPDATE provinces SET code = ?, name = ?, region_id = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null,
                province.getId()
        );

        logger.info("Actualizado provincias. Lines afectadas: {}", rowsAffected);
    }

    @Override
    public Province getProvinceById(Long id) {
        logger.info("Retrieving province by id: {}", id);
        String sql =  "SELECT p.id, p.code, p.name, r.id as region_id, r.code as region_code, r.name as region_name" +
                " FROM provinces p " +
                "JOIN regions r ON p.region_id = r.id" +
                " WHERE p.id=?";
        try{
            Province province = jdbcTemplate.queryForObject(sql, provinceRowMapper, id);
            if (province != null){
                logger.info("Province retrieved: {} - {}", province.getCode(), province.getName() );
            }
            return province;
        }catch (Exception e){
            logger.warn("No province found with id: {}", id);
            return null;
        }

    }

    @Override
    public void deleteProvince(Long id){
        logger.error("Eliminando provincia con id: {}", id);
        String sql = "DELETE FROM provinces WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Provincia eliminada. Lines afectadas: {}", rowsAffected);

    }

}
