package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    boolean existsByCode(Long id);
    boolean existsByCodeAndIdNot(String code, Long id);
    @Override
    Optional<Province> findById(Long id);


}
