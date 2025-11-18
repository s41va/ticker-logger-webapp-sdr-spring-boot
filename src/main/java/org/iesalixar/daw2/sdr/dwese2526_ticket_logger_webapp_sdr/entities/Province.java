package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La clase {@code Province} representa una provincia dentro del sistema.
 *
 * Cada provincia tiene:
 * <ul>
 * <li>{@code id}: identificador único (clave primaria, autogenerado en BD).</li>
 * <li>{@code code}: código único de la provincia (ej. "SE", "MA", "GR").</li>
 * <li>{@code name}: nombre de la provincia (ej. "Sevilla").</li>
 * <li>{@code region}: objeto {@link Region} al que pertenece la provincia
 * (que equivale a la FK {@code region_id} en la base de datos).</li>
 * </ul>
 *
 * Se sigue el mismo estilo que {@link Region}, usando Lombok y Bean Validation.
 * A nivel de BD seguirá existiendo la columna {@code region_id}, pero en la capa
 * de dominio trabajamos con el objeto {@link Region}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province {

    // Identificador único de la provincia (AUTO_INCREMENT en la tabla 'provinces').
    private Long id;

    // Código único de la provincia (VARCHAR(10) NOT NULL UNIQUE).
    @NotEmpty(message = "{msg.province.code.notEmpty}")
    @Size(max = 10, message = "{msg.province.code.size}")
    private String code;

    // Nombre de la provincia (VARCHAR(100) NOT NULL).
    @NotEmpty(message = "{msg.province.name.notEmpty}")
    @Size(max = 100, message = "{msg.province.name.size}")
    private String name;



    private Region region;

    @AssertTrue(message = "{msg.province.region.notnull}")
    public boolean isRegionSelected(){
        return region != null & region.getId()!=null;
    }


    public Province(String code, String name, Region region) {
        this.code = code;
        this.name = name;
        this.region = region;
    }
}