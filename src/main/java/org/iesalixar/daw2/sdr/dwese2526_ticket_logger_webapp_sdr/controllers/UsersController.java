package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos.UsersDAO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
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
        List<Users> listUsers = null;
        try {
            listUsers = usersDAO.listAllUsers();
            logger.info("Se han devuelto {} usuarios.", listUsers.size());
        } catch (Exception e) {
            logger.error(" Error al listar los usuarios: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar los usuarios.");
        }
        model.addAttribute("listUsers", listUsers);
        return "views/users/user-list";
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
        model.addAttribute("user", new Users());
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
        Users user = null;

        try {
            user = usersDAO.getUsersById(id);
            if (user == null) {
                logger.warn(" No se ha encontrado el usuario con Id {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
                return "redirect:/users";
            }
        } catch (Exception e) {
            logger.error(" Error al obtener el usuario con Id {} :{}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el usuario.");
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "views/users/user-form";
    }

    // --- MÉTODOS POST: INSERTAR, ACTUALIZAR, ELIMINAR ---

    /**
     * Inserta un nuevo usuario en la base de datos. (Equivalente a doPost, action=insert)
     * URL: /users/insert
     *
     * @param user El objeto Users con los datos del formulario (debe tener las validaciones JSR-303).
     * @param result El resultado del proceso de validación.
     * @param redirectAttributes Atributos para mensajes flash (mensajes de éxito/error después de la redirección).
     * @param locale La configuración regional para mensajes internacionalizados.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/insert")
    public String insertUsers(@ModelAttribute("user") Users user, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info(" Insertando nuevo usuario: {}", user.getUsername());

        // **Validaciones JSR-303 (si estuvieran implementadas en Users.java)**
        if (result.hasErrors()) {
            return "views/users/user-form"; // Vuelve al formulario con errores de campo
        }

        try {
            // **Validación de unicidad de username**
            if (usersDAO.existsUserByUsername(user.getUsername())) {
                logger.warn("El username {} ya existe.", user.getUsername());
                // Usar messageSource para el mensaje de error si está configurado
                // String errorMessage = messageSource.getMessage("msg.user-controller.insert.usernameExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", "El username ya existe. Por favor, elija otro.");
                redirectAttributes.addFlashAttribute("user", user); // Mantener datos
                return "redirect:/users/new";
            }

            // **Lógica de negocio del UserServlet: Calcular passwordExpiresAt**
            if (user.getLastPasswordChange() != null) {
                user.setPasswordExpiresAt(user.getLastPasswordChange().plusMonths(3));
            } else {
                // Si la fecha es null, se establece la actual y se calcula la expiración
                LocalDateTime now = LocalDateTime.now();
                user.setLastPasswordChange(now);
                user.setPasswordExpiresAt(now.plusMonths(3));
            }

            usersDAO.insertUser(user);
            logger.info(" Usuario '{}' insertado con éxito.", user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado con éxito.");

        } catch (Exception e) {
            logger.error(" Error al insertar el usuario {}: {}", user.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el usuario. Por favor, revise los datos.");
            // Agregar el objeto 'user' de nuevo para rellenar el formulario
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/users/new";
        }
        return "redirect:/users";
    }

    /**
     * Actualiza un usuario existente en la base de datos. (Equivalente a doPost, action=update)
     * URL: /users/update
     *
     * @param user Objeto Users con los datos actualizados.
     * @param result Resultado de la validación.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/update")
    public String updateUsers(@ModelAttribute("user") Users user, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info(" Actualizando usuario con ID {}", user.getId());

        // **Validaciones JSR-303 (si estuvieran implementadas en Users.java)**
        if (result.hasErrors()) {
            return "views/users/user-form"; // Vuelve al formulario con errores de campo
        }

        try {
            // **Validación de unicidad de username (excluyendo el ID actual)**
            if (usersDAO.existsUserByUsernameAndNotId(user.getUsername(), user.getId())) {
                logger.warn("El username {} ya existe para otro usuario.", user.getUsername());
                redirectAttributes.addFlashAttribute("errorMessage", "El username ya existe para otro usuario.");
                return "redirect:/users/edit?id=" + user.getId();
            }

            // **Lógica de negocio del UserServlet: Recalcular passwordExpiresAt**
            if (user.getLastPasswordChange() != null) {
                user.setPasswordExpiresAt(user.getLastPasswordChange().plusMonths(3));
            } else {
                // Debería ser muy raro en un update, pero previene NPE
                user.setLastPasswordChange(LocalDateTime.now());
                user.setPasswordExpiresAt(LocalDateTime.now().plusMonths(3));
            }

            usersDAO.updateUsers(user);
            logger.info(" Usuario con ID {} actualizado con éxito.", user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado con éxito.");

        } catch (Exception e) {
            logger.error(" Error al actualizar el usuario con ID {}: {}", user.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el usuario. Revise los datos.");
            return "redirect:/users/edit?id=" + user.getId();
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