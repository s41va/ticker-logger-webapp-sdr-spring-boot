package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegionService {
    Page<RegionDTO> list(Pageable pageable);
    RegionUpdateDTO getForEdit(Long id);
    void create(RegionCreateDTO dto);
    void update(RegionUpdateDTO dto);
    void delete(Long id);
    RegionDetailDTO getDetail( Long id);
}
