package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProvinceService {

    Page<ProvinceDTO> list(Pageable pageable);
    ProvinceUpdateDTO getForEdit(Long id);
    void create(ProvinceCreateDTO dto);
    void update(ProvinceUpdateDTO dto);
    void delete(Long id);
    ProvinceDetailDTO getDetail(Long id);
    List<RegionDTO> listRegionsForSelect();



}
