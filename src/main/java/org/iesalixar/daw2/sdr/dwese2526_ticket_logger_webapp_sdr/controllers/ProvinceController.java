package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.controllers;



import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import jakarta.validation.Valid;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.dtos.*;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities.Region;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.ProvinceMapper;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.mappers.RegionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories.ProvinceRepository;
import org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.repositories.RegionRepository;
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
import java.util.Optional;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {
    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public String listProvinces(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                                Model model, Locale locale){
        logger.info("Solicitando la lista de todas las provincias page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try{
            Page<ProvinceDTO> listProvinceDTOs = provinceRepository.findAll(pageable).map(ProvinceMapper::toDTO);
            logger.info("Se han cargado {} regiones en la pagina {}",
                    listProvinceDTOs.getNumberOfElements(), listProvinceDTOs.getNumber());
            model.addAttribute("page", listProvinceDTOs);
            String sortParam = "name,asc";
            if (listProvinceDTOs.getSort().isSorted()){
                Sort.Order order = listProvinceDTOs.getSort().iterator().next();
                sortParam= order.getProperty() + " " + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);

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
            Optional<Province> provinceOpt = provinceRepository.findById(id);
            if (provinceOpt.isEmpty()){
                logger.warn("No se encontró ninguna provincia con ID: {}", id);
                String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/provinces";
            }
            Province province = provinceOpt.get();

            ProvinceDetailDTO provinceDTO = ProvinceMapper.toDetailDTO(province);

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
            List<Region> listRegions = regionRepository.findAll();
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
                List<Region> listRegions = regionRepository.findAll();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegions);
                return "views/province/province-form";
            }

            if (provinceRepository.existsByCode(provinceDTO.getCode())) {
                logger.warn("El código de la provincia {} ya existe", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }
            Province province = ProvinceMapper.toEntity(provinceDTO);
            provinceRepository.save(province);
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
        Optional<Province> provinceOpt;
        ProvinceUpdateDTO provinceUpdateDTO = null;
        try{
            provinceOpt = provinceRepository.findById(id);

            if (provinceOpt.isEmpty()){
                logger.warn("No se ha encontrado ninguna provincia con id: {}", id);
                String errorMessage = messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
                model.addAttribute("errorMessage", errorMessage);

            }else {
                Province province = provinceOpt.get();
                provinceUpdateDTO = ProvinceMapper.toUpdateDTO(province);
                List<Region> listRegions = regionRepository.findAll();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("province", provinceUpdateDTO);
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
                List<Region> listRegions = regionRepository.findAll();
                List<RegionDTO> listRegionsDTO = RegionMapper.toDTOList(listRegions);
                model.addAttribute("listRegions", listRegionsDTO);
                return "views/provinces/province-form";
            }

            if (provinceRepository.existsByCodeAndIdNot(provinceDTO.getCode(), provinceDTO.getId())) {
                logger.warn("El código de la provincia {} ya existe para otra provincia.", provinceDTO.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/edit/?id=" + provinceDTO.getId();
            }
            Optional<Province> provinceOpt = provinceRepository.findById(provinceDTO.getId());
            if (provinceOpt.isEmpty()){
                logger.warn("No se encontró la region con ID: {}", provinceDTO.getId());
                String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/provinces";
            }
            Province province = provinceOpt.get();
            ProvinceMapper.copyToExistingEntity(provinceDTO, province);
            provinceRepository.save(province);
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
            Optional<Province> provinceOpt = provinceRepository.findById(id);
            if (provinceOpt.isEmpty()){
                logger.warn("No se encontro la provincia con id {}", id);
                String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", notFound);
                return "redirect:/provinces";
            }
        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/provinces";
    }
}
