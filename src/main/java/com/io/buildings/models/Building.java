package com.io.buildings.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Building extends Localization {

    @OneToMany()
    @JoinColumn(name = "building_id")
    private List<Floor> floors;

    public Building(String name, List<Floor> floors) {
        super(name);
        this.floors = floors;
    }

    @Override
    public Float countSurface(){
        return floors.stream().map(Localization::countSurface).reduce(0f,Float::sum);
    }
}
