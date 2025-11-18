package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Province;

import java.util.List;

public interface ProvinceDAO {
    List<Province> listAllProvinces();
    void insertProvince(Province province);
    void updateProvince(Province province);
    void deleteProvince(Long id);
    Province getProvinceById(Long id);
    boolean existsProvinceByCode(String code);
    boolean existsProvinceByCodeAndNotId(String code, Long id);

}
