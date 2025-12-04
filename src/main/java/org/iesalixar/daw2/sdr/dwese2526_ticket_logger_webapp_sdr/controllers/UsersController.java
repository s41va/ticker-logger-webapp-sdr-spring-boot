package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos.UsersDAO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.UsersUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.User;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.UsersMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * 💻 Controlador Spring MVC para la gestión de usuarios (CRUD).
 * <p>
 * Migra la funcionalidad del UserServlet a un entorno Spring, utilizando
 * inyección de dependencias para el UsersDAO y manejando las solicitudes
 * mediante anotaciones.
 * </p>
 *
 * @author Salvador Díaz Román (Adaptado a Spring Controller)
 * @version 2.0
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private MessageSource messageSource; // Para mensajes de internacionalización/error

    // --- MÉTODOS GET: LISTAR, NUEVO, EDITAR ---

    /**
     * Muestra la lista de todos los usuarios. (Equivalente a doGet, action=list)
     * URL: /users
     *
     * @param model El objeto Model para pasar datos a la vista.
     * @return La ruta a la vista JSP de lista de usuarios.
     */
    @GetMapping
    public String listUsers(Model model) {
        logger.info(" Solicitando la lista de todos los usuarios...");
        List<User> listUsers = null;
        List<UsersDTO> listUsersDTOs = null;
        try {
            listUsers = usersDAO.listAllUsers();
            listUsersDTOs = UsersMapper.toDTOList(listUsers);
            logger.info("Se han devuelto {} usuarios.", listUsersDTOs.size());
        } catch (Exception e) {
            logger.error(" Error al listar los usuarios: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }
        model.addAttribute("listUsers", listUsersDTOs);
        return "views/users/user-list";
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id")Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale){
        logger.info("Mostrando detalle de la region con ID: {}", id);
        try{
            User users = usersDAO.getUsersById(id);
            if (users == null){
                String msg = messageSource.getMessage("msg.user-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/users";
            }
            UsersDetailDTO userDTO = UsersMapper.toDetailDTO(users);
            model.addAttribute("user", userDTO);
            return "views/users/user-detail";
        }catch (Exception e){
            logger.error("Error al obtener el detalle de la region {} : {}", id, e.getMessage(),e);
            String msg = messageSource.getMessage("msg.user-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/users";
        }
    }


    /**
     * Muestra el formulario para crear un nuevo usuario. (Equivalente a doGet, action=new)
     * URL: /users/new
     *
     * @param model El objeto Model para pasar un objeto `Users` vacío al formulario.
     * @return La ruta a la vista JSP del formulario de usuario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info(" Mostrando el formulario para nuevo usuario.");
        // Se crea un objeto Users vacío para enlazar los datos del formulario
        model.addAttribute("user", new UsersCreateDTO());
        return "views/users/user-form";
    }

    /**
     * Muestra el formulario para editar un usuario existente. (Equivalente a doGet, action=edit)
     * URL: /users/edit?id=X
     *
     * @param id El ID del usuario a editar, tomado del parámetro de la URL.
     * @param model El objeto Model para pasar el usuario a la vista.
     * @return La ruta a la vista JSP del formulario de usuario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info(" Entrando al método showEditForm para ID: {}", id);
        User user = null;
        UsersUpdateDTO usersDTO = null;
        try {
            user = usersDAO.getUsersById(id);
            if (user == null) {
                logger.warn(" No se ha encontrado el usuario con Id {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
                return "redirect:/users";
            }
            usersDTO = UsersMapper.toUpdateDTO(user);
        } catch (Exception e) {
            logger.error(" Error al obtener el usuario con Id {} :{}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el usuario.");
            return "redirect:/users";
        }
        model.addAttribute("user", usersDTO);
        return "views/users/user-form";
    }

    // --- MÉTODOS POST: INSERTAR, ACTUALIZAR, ELIMINAR ---

    /**
     * Inserta un nuevo usuario en la base de datos. (Equivalente a doPost, action=insert)
     * URL: /users/insert
     *
     * @param userDTO El objeto Users con los datos del formulario (debe tener las validaciones JSR-303).
     * @param result El resultado del proceso de validación.
     * @param redirectAttributes Atributos para mensajes flash (mensajes de éxito/error después de la redirección).
     * @param locale La configuración regional para mensajes internacionalizados.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/insert")
    public String insertUsers(@ModelAttribute("user")
                                  UsersCreateDTO userDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes, Locale locale) {
        logger.info(" Insertando nuevo usuario: {}", userDTO.getEmail());


        try {

            if (result.hasErrors()) {
                return "user-form"; // Vuelve al formulario con errores de campo
            }

            // **Validación de unicidad de username**
            if (usersDAO.existsUserByEmail(userDTO.getEmail())) {
                logger.warn("El email {} ya existe.", userDTO.getEmail());
                // Usar messageSource para el mensaje de error si está configurado
                String errorMessage = messageSource.getMessage("msg.user-controller.insert.usernameExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage); // Mantener datos
                return "redirect:/users/new";
            }

            // **Lógica de negocio del UserServlet: Calcular passwordExpiresAt**
            if (userDTO.getLastPasswordChange() != null) {
                userDTO.setPasswordExpiresAt(userDTO.getLastPasswordChange().plusMonths(3));
            } else {
                // Si la fecha es null, se establece la actual y se calcula la expiración
                LocalDateTime now = LocalDateTime.now();
                userDTO.setLastPasswordChange(now);
                userDTO.setPasswordExpiresAt(now.plusMonths(3));
            }
            User user = UsersMapper.toEntity(userDTO);
            usersDAO.insertUser(user);
            logger.info(" Usuario '{}' insertado con éxito.", user.getEmail());
            String successMessage = messageSource.getMessage("msg.user-controller.insert.success", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        } catch (Exception e) {
            logger.error(" Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            // Agregar el objeto 'user' de nuevo para rellenar el formulario

            return "redirect:/users/new";
        }
        return "redirect:/users";
    }

    /**
     * Actualiza un usuario existente en la base de datos. (Equivalente a doPost, action=update)
     * URL: /users/update
     *
     * @param userDTO Objeto Users con los datos actualizados.
     * @param result Resultado de la validación.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/update")
    public String updateUsers(@Valid @ModelAttribute("user") UsersUpdateDTO userDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info(" Actualizando usuario con ID {}", userDTO.getId());

        try {
            // **Validaciones JSR-303 (si estuvieran implementadas en Users.java)**
            if (result.hasErrors()) {
                return "views/users/user-form"; // Vuelve al formulario con errores de campo
            }
            // **Validación de unicidad de username (excluyendo el ID actual)**
            if (usersDAO.existsUserByEmailAndNotId(userDTO.getEmail(), userDTO.getId())) {
                logger.warn("El email {} ya existe para otro usuario.", userDTO.getEmail());
                String errorMessage = messageSource.getMessage("msg.user-controller.update.userExists", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/edit?id=" + userDTO.getId();
            }

            User existingUser = usersDAO.getUsersById(userDTO.getId());

            if (existingUser == null){
                return "redirect:/users";
            }

            UsersMapper.copyToExistingEntity(userDTO, existingUser);
            usersDAO.updateUsers(existingUser);
            logger.info(" Usuario con ID {} actualizado con éxito.", existingUser.getId());


        } catch (Exception e) {
            logger.error(" Error al actualizar el usuario con ID {}: {}", userDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        }
        return "redirect:/users";
    }

    /**
     * Elimina un usuario de la base de datos. (Equivalente a doPost/deleteUsers)
     * URL: /users/delete
     *
     * @param id El ID del usuario a eliminar.
     * @param redirectAttributes Atributos para mensajes flash.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/delete")
    public String deleteUsers(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.warn(" Entrando al método deleteUsers para ID: {}", id);

        try {
            usersDAO.deleteUsers(id);
            logger.info(" Usuario con ID {} eliminado con éxito", id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            logger.error(" Error al eliminar el usuario con ID {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario.");
        }
        return "redirect:/users";
    }
}