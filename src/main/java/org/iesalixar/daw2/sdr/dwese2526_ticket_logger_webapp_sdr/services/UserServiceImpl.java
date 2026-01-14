package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.services;

import jakarta.transaction.Transactional;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public Page<UsersDTO> list(Pageable pageable) {
        return null;
    }

    @Override
    public RegionUpdateDTO getForEdit(Long id) {
        return null;
    }

    @Override
    public void create(UsersCreateDTO dto) {

    }

    @Override
    public void update(UsersUpdateDTO dto) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public UsersDetailDTO getDetail(Long id) {
        return null;
    }
}
