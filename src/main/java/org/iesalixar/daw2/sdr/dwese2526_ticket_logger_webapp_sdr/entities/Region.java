package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_webapp_sdr.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="regions")
public class Region {

    @Id
    @GeneratedValue
    private Long id;


    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    @Column(name="code", nullable = false, length = 2)
    private String code;



    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.notEmpty}")
    @Column(name="name", nullable = false, length = 100)
    private String name;

    @OneToMany(
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = false
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Province> provinces = new ArrayList<>();



    public Region(String code, String name){
        this.code=code;
        this.name=name;
    }



}