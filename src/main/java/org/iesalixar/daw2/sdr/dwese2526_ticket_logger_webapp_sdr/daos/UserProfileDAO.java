package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos;

import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.UserProfile;

public interface UserProfileDAO {

    UserProfile getUserProfileByUserId(Long userId);
    void saveOrUpdateUserProfile(UserProfile userProfile);
    boolean existsUserProfileByUserId(Long userId);
}
