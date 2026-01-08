package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories.UserProfileRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories.UsersRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UserProfileFormDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.UserProfile;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.UserProfileMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UsersRepository userDao;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;


    @GetMapping("/edit")
    public String showProfileForm(Model model, Locale locale){
        final String fixedEmail = "admin@app.local";
        logger.info("Mostrando formulario de perfil para el usuario fijo {}", fixedEmail);

        User user = userDao.getUserByEmail(fixedEmail);

        if (user == null){
            logger.warn("No se encontró usuario con email: {}", fixedEmail);
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.notFound", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            return "views/user-profile/user-profile-form";

        }

        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
        UserProfileFormDTO userProfileFormDTO = UserProfileMapper.toFormDto(user, userProfile);
        model.addAttribute("userProfileForm", userProfileFormDTO);



        return "views/user-profile/user-profile-form";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("userProfileForm")
                                @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
                                UserProfileFormDTO profileDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Locale locale){

        logger.info("Actualizando Profile con id: {}", profileDto.getUserId());
        if (result.hasErrors()){
            logger.warn("Errores de validacion en el formulario de perfil para userID={}", profileDto.getUserId());
            return "views/user-profile/user-profile-form";
        }

        try{
            Long userId = profileDto.getUserId();
            User user = userDao.getUsersById(userId);

            if (user == null){
                logger.warn("No se encontro usuario con id = {}", userId);
                String errorMessage = messageSource.getMessage("msg.user-controller.edit.notFound", null, locale);

                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/profile/edit";
            }

            UserProfile userProfile = userProfileRepository.getUserProfileByUserId(userId);
            boolean isNew = (userProfile == null);
            if (isNew){
                userProfile = UserProfileMapper.toNewEntity(profileDto, user);
            }else {
                UserProfileMapper.copyToExistingEntity(profileDto,userProfile);
            }


            // 4. Gestión de la imagen de perfil (si se ha subido una nueva)
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                logger.info("Se ha subido un nuevo archivo de imagen para el perfil del usuario {}", userId);

                // Validación de tipo MIME
                String contentType = profileImageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    logger.warn("Archivo de tipo no permitido: {}", contentType);
                    String msg = messageSource.getMessage("msg.userProfile.image.invalidType", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    return "redirect:/profile/edit";
                }

                // Validación de tamaño (ejemplo: 2MB máximo)
                long maxSizeBytes = 2 * 1024 * 1024; // 2 MB
                if (profileImageFile.getSize() > maxSizeBytes) {
                    logger.warn("Archivo demasiado grande: {} bytes (límite {} bytes)",
                            profileImageFile.getSize(), maxSizeBytes);
                    String msg = messageSource.getMessage("msg.userProfile.image.tooLarge", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    return "redirect:/profile/edit";
                }

                // Si llega aquí, el archivo pasa las validaciones y lo guardamos
                String oldImagePath = profileDto.getProfileImage(); // ruta actual (puede ser null)

                String newImageWebPath = fileStorageService.saveFile(profileImageFile);
                if (newImageWebPath == null) {
                    logger.error("No se pudo guardar la nueva imagen de perfil para el usuario {}", userId);
                    String msg = messageSource.getMessage("msg.userProfile.image.saveError", null, locale);
                    redirectAttributes.addFlashAttribute("errorMessage", msg);
                    return "redirect:/profile/edit";
                }

                logger.info("Nueva imagen de perfil guardada en {}", newImageWebPath);

                // Actualizar en el DTO la ruta de la imagen
                profileDto.setProfileImage(newImageWebPath);

                if (oldImagePath != null && !oldImagePath.isBlank()){
                    logger.info("Eliminando imagen anterior del perfil: {}", oldImagePath);
                    fileStorageService.deleteFile(oldImagePath);
                }
            }
            if (isNew){
                userProfile = UserProfileMapper.toNewEntity(profileDto, user);
            }else {
                UserProfileMapper.copyToExistingEntity(profileDto, userProfile);
            }
            userProfileRepository.saveOrUpdateUserProfile(userProfile);
            String successMessage = messageSource.getMessage("msg.userProfile.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        }catch (Exception e){
            logger.error("Error al actualizar el perfil de Usuario con id: {} : {}", profileDto.getUserId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.userProfile.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        }

        return "redirect:/profile/edit";
    }



}
