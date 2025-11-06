package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Region {


    private Long id;


    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;



    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.notEmpty}")
    private String name;

    public Region(String code, String name){
        this.code=code;
        this.name=name;
    }
}