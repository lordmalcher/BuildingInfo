package com.io.buildings.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * model on which based is database table. Extends Localization
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Room extends Localization {

    @Column(nullable = false)
    private Float area;

    @Column(nullable = false)
    private Float cube;

    @Column(nullable = false)
    private Float heating;

    @Column(nullable = false)
    private Float light;

    public Room(String name, Float area, Float cube, Float heating, Float light) {
        super(name);
        this.area = area;
        this.cube = cube;
        this.heating = heating;
        this.light = light;
    }
    /**
     * Overrided method for printing objects of this class
     * @return String
     */
    @Override
    public String toString(){
        return "name:"+super.getName()+" area:"+area+" cube:"+cube+" heating:"+heating+" light:"+light+" ";
    }
    /**
     * Overrided method for comparison objects of this class
     * @param obj Object: object for comparison
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return super.getName() == ((Room)obj).getName() && this.area == ((Room)obj).getArea() && this.cube == ((Room)obj).getCube() &&
                this.heating == ((Room)obj).getHeating() && this.light == ((Room)obj).getLight();
    }
    /**
     * returns surface of room
     * @return surface
     */
    @Override
    public Float countSurface() {
        return area;
    }
    /**
     * returns cube of room
     * @return cube
     */
    @Override
    public Float countCube() {
        return cube;
    }
    /**
     * returns average light of room
     * @return average light
     */
    @Override
    public Float countAverageLight() {
        return light/area;
    }
    /**
     * returns average heating of room
     * @return average heating
     */
    @Override
    public Float countAverageHeating(){return heating/area;}
    /**
     * select rooms which heating is higher than given value
     * @param value float: limit of heating
     * @return list of rooms
     */
    @Override
    public List<Room> getLocalizationAboveHeating(float value){
        List<Room> list = new ArrayList<Room>();
        if (heating > value) {
            list.add(this);
        }
        return list;
    }
    /**
     * select rooms which surface is in the range of given values
     * @param leftValue float: left bound
     * @param rightValue float: right bound
     * @return list of rooms
     */
    @Override
    public List<Room> getLocalizationBySurface(float leftValue,float rightValue){
        List<Room> list = new ArrayList<>();
        if( area >= leftValue && area <= rightValue){
            list.add(this);
        }
        return list;
    }
}
