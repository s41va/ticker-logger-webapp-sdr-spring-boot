package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.services;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UsersDTO> list(Pageable pageable);
    RegionUpdateDTO getForEdit(Long id);
    void create(UsersCreateDTO dto);
    void update(UsersUpdateDTO dto);
    void delete(Long id);
    UsersDetailDTO getDetail(Long id);
}
