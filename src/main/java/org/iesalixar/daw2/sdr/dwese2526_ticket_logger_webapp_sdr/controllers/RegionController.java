package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionCreateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionDetailDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.RegionMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.ui.Model;
import org.apache.catalina.LifecycleState;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.daos.RegionDAO;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/regions")
public class RegionController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionDAO regionDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listRegions(@RequestParam(name="page", defaultValue = "0")int page,
                              @RequestParam(name = "size", defaultValue = "10")int size,
                              @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                              @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                              Model model) {
        logger.info("Solicitando la lista de todas las regiones... page={}, size={}, sortField={}, sortDir={}",
                page, size, sortField, sortDir);
        if (page<0) page = 0;
        if (size<=0) size = 10;
        try {
           long totalElements = regionDAO.countRegion();
           int totalPages = (int) Math.ceil((double) totalElements/size);

           if (totalPages > 0 && page >= totalPages){
               page = totalPages -1;
           }
           List<Region> listRegion = regionDAO.listAllRegionsPage(page, size, sortField, sortDir);
           List<RegionDTO> listRegionsDTOs = RegionMapper.toDTOList(listRegion);
            logger.info("Se han cargado {} regiones en la página {}", listRegionsDTOs.size(), page);
            model.addAttribute("ListRegions", listRegionsDTOs);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSorDir","asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las regiones");
        }
        return "views/region/region-list";


    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id")Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale){
        logger.info("Mostrando detalle de la region con ID: {}", id);
        try{
            Region region = regionDAO.getRegionById(id);
            if (region == null){
                String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/regions";
            }
            RegionDetailDTO regionDTO = RegionMapper.toDetailDTO(region);
            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";
        }catch (Exception e){
            logger.error("Error al obtener el detalle de la region {} : {}", id, e.getMessage(),e);
            String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }

    /**
     * Inserta una nueva región en la base de datos.
     * @param regionDTO que contiene los datos del formulario.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/insert")
    public String insertRegion(@Valid @ModelAttribute("region") RegionCreateDTO regionDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva región con código {}", regionDTO.getCode());
        try {
            if (result.hasErrors()) {
                return "region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (regionDAO.existsRegionByCode(regionDTO.getCode())) {
                logger.warn("El código de la región {} ya existe.", regionDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/regions/new";
            }
            Region region = RegionMapper.toEntity(regionDTO);
            regionDAO.insertRegion(region);
            logger.info("Región {} insertada con éxito.", region.getCode());
        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", regionDTO.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }


    /**
     * Actualiza una región existente en la base de datos.
     * @param redirectAttributes  Atributos para mensajes flash de redirección.
     * @return Redirección a la lista de regiones.
     */
    @PostMapping("/update")
    public String updateRegion(@Valid @ModelAttribute("region") RegionUpdateDTO regionDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando región con ID {}", regionDTO.getId());
        try {
            if (result.hasErrors()) {
                return "region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (regionDAO.existsRegionByCodeAndNotId(regionDTO.getCode(), regionDTO.getId())) {
                logger.warn("El código de la región {} ya existe para otra región.", regionDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/regions/edit?id=" + regionDTO.getId();
            }
            Region region = regionDAO.getRegionById(regionDTO.getId());
            if (region == null){
                logger.warn("No se encontró la region con ID: {}", regionDTO.getId());
                String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/regions";
            }
            RegionMapper.copyToExistingEntity(regionDTO, region);
            regionDAO.updateRegion(region);
            logger.info("Región con ID {} actualizada con éxito.", region.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID {}: {}", regionDTO.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }




    @GetMapping("/new")
    public String showNewForm(Model model){
        logger.info("Mostando el nuevo formilario de regiones");
        model.addAttribute("region", new RegionCreateDTO());
        return "views/region/region-form";
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam ("id") Long id, Model model){
        logger.info("Entrando al metodo showEditForm");
        Region region  = null;
        RegionUpdateDTO regionDTO = null;

        try{
            region = regionDAO.getRegionById(id);
            if (region == null){
                logger.warn("No se ha encontrado la region con Id {}" , id);
            }
            regionDTO = RegionMapper.toUpdateDTO(region);
        }catch (Exception e){
            logger.error("Error al obtener la region con Id {} :{}", id ,e.getMessage());
            model.addAttribute("errorMessage", "Error al obtener la region");
        }
        model.addAttribute("region", regionDTO);
        return "views/region/region-form";

    }



    @PostMapping("/delete")
    public String deleteRegion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){
        logger.info("Entrando al metodo deleteRegion");

        try{
            regionDAO.deleteRegion(id);
            logger.info("Region con ID {} eliminada con exito", id);
        }catch (Exception e){
            logger.error("Error al eliminar la region con ID {} : {}", id , e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la region");
        }
        return "redirect:/regions";
    }

}





























