package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;



import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.ProvinceMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.RegionMapper;
import org.springframework.ui.Model;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos.ProvinceDAO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos.RegionDAO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {
    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceDAO provinceDAO;

    @Autowired
    private RegionDAO regionDAO;

    @GetMapping
    public String listProvinces(@RequestParam(name = "page", defaultValue = "0")int page,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                                @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                                Model model, Locale locale){
        logger.info("Solicitando la lista de todas las provincias");
        if (page<0) page = 0;
        if (size<=0) size = 10;
        try{
            long totalElements = provinceDAO.countProvince();
            int totalPages = (int) Math.ceil((double) totalElements/size);

            if (totalPages > 0 && page >= totalPages){
                page = totalPages -1;
            }

            List<Province> entities = provinceDAO.listProvincesPage(page, size, sortField, sortDir);
            List<ProvinceDTO> listProvinceDTOs = ProvinceMapper.toDTOList(entities);
            logger.info("Se han cargado {} regiones en la página {}", listProvinceDTOs.size(), page);
            model.addAttribute("listProvinces", listProvinceDTOs);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSorDir","asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");

        } catch (Exception e) {
            logger.error("Error al insertar las provincias: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.list.error" ,null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-list";
    }


    @GetMapping("/detail")
    public String showDetail(@RequestParam("id")Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale){
        logger.info("Mostrando detalle de la provincia con ID: {}", id);
        try{
            Province province = provinceDAO.getProvinceById(id);
            ProvinceDetailDTO provinceDTO = ProvinceMapper.toDetailDTO(province);
            if (province == null){
                logger.warn("No se encontró ninguna provincia con ID: {}", id);
                String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/provinces";
            }
            model.addAttribute("province", provinceDTO);
            return "views/province/province-detail";
        }catch (Exception e){
            logger.error("Error al obtener el detalle de la provincia {} : {}", id, e.getMessage(),e);
            String msg = messageSource.getMessage("msg.province-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }

    @GetMapping("/new")
    public String showNewForm( Model model, Locale locale){
        logger.info("Entrando al metodo showNewForm");
        try{
            List<Region> listRegions =regionDAO.listAllRegions();
            List<RegionDTO> listRegionDTOs = RegionMapper.toDTOList(listRegions);
            model.addAttribute("province" , new ProvinceCreateDTO());
            model.addAttribute("listRegions", listRegionDTOs);
        }catch (Exception e){
            logger.error("Error al cargar las regiones para el formulario de provincia: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);

        }
        return "views/province/province-form";
    }

    /**
     * Inserta una nueva provincia en la base de datos.
     *
     * @param provinceDTO Objeto con los datos del formulario.
     * @param result Resultado de la validación.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional actual para mensajes.
     * @return Redirección al listado de provincias.
     */
    @PostMapping("/insert")
    public String insertProvince(@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Insertando nueva provincia con código {}", provinceDTO.getCode());

        try {
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegions);
                return "views/province/province-form";
            }

            if (provinceDAO.existsProvinceByCode(provinceDTO.getCode())) {
                logger.warn("El código de la provincia {} ya existe", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.insertProvince(province);
            logger.info("Provincia {} insertada con éxito.", province.getCode());

        } catch (Exception e) {
            logger.error("Error al insertar la provincia {}: {}", provinceDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/provinces";
    }

    /**
     * Muestra el formulario para editar una provincia existente.
     *
     * @param id ID de la provincia a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale){
        logger.info("Mostrando formulario de edicion para la provincia con ID: {}", id);
        try{
            Province province = provinceDAO.getProvinceById(id);
            ProvinceUpdateDTO provinceDTO = ProvinceMapper.toUpdateDTO(province);
            if (province==null){
                logger.warn("No se ha encontrado ninguna provincia con id: {}", id);
                String errorMessage = messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
                model.addAttribute("errorMessage", errorMessage);

            }else {
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("province", provinceDTO);
                model.addAttribute("listRegions", listRegionsDTO);
            }
        } catch (Exception e) {
            logger.error("Erroe al obtener la provincia con id: {}, {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);

        }
        return "views/province/province-form";
    }
    /**
     * Actualiza una provincia existente en la base de datos.
     *
     * @param provinceDTO Provincia con los datos actualizados.
     * @param result Resultado de la validación.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional actual.
     * @return Redirección al listado de provincias.
     */


    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Actualizando provincia con ID {}", provinceDTO.getId());
        try {
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                List<Region> listRegions = regionDAO.listAllRegions();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegionsDTO);
                return "views/provinces/province-form";
            }

            if (provinceDAO.existsProvinceByCodeAndNotId(provinceDTO.getCode(), provinceDTO.getId())) {
                logger.warn("El código de la provincia {} ya existe para otra provincia.", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/edit/?id=" + provinceDTO.getId();
            }

            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceDAO.updateProvince(province);
            logger.info("Provincia con ID {} actualizada con éxito.", province.getId());

        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID {}: {}", provinceDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/provinces";
    }

    /**
     * Elimina una provincia de la base de datos.
     *
     * @param id ID de la provincia a eliminar.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale Configuración regional actual.
     * @return Redirección al listado de provincias.
     */
    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {

        logger.info("Eliminando provincia con ID {}", id);
        try {
            provinceDAO.deleteProvince(id);
            logger.info("Provincia con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/provinces";
    }
}
