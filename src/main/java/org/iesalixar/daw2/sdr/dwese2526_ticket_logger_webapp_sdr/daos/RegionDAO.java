package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;

import java.sql.SQLException;
import java.util.List;

public interface RegionDAO {

    List<Region> listAllRegions() ;
    List<Region> listAllRegionsPage(int page, int size, String sortField, String sortDir);
    long countRegion();

    void insertRegion(Region region) ;

    boolean existsRegionByCode(String code);

    void updateRegion(Region region) ;
    Region getRegionById(Long id) ;
    boolean existsRegionByCodeAndNotId(String code, Long id) ;
    void deleteRegion(Long id) ;
}