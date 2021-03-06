package com.io.buildings.controllers;

import com.io.buildings.models.Building;
import com.io.buildings.models.Floor;
import com.io.buildings.models.Localization;
import com.io.buildings.models.Room;
import com.io.buildings.repositories.BuildingRepository;
import com.io.buildings.repositories.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.io.buildings.controllers.requests.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private static final String BUILDING_NOT_FOUND = "Building with given Id doesn't exists";
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;

    @Autowired
    public BuildingController(BuildingRepository buildingRepository, FloorRepository floorRepository) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public Building getInfo(@PathVariable Integer id) {
        return buildingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/surface")
    public Float getSurface(@PathVariable Integer id) {
        return buildingRepository.findById(id).map(Localization::countSurface
        ).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/cube")
    public Float getCube(@PathVariable Integer id) {
        return buildingRepository.findById(id).map(Localization::countCube
        ).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/light")
    public Float getAverageLight(@PathVariable Integer id) {
        return buildingRepository.findById(id).map(Localization::countAverageLight
        ).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/heating")
    public Float getAverageHeating(@PathVariable Integer id) {
        return buildingRepository.findById(id).map(Localization::countAverageHeating
        ).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/aboveHeating")
    public List<Room> getLocalizationAboveHeating(@RequestParam Float value) {
        List<Room> list = new ArrayList<>();
        List<Building> listBuilding = new ArrayList<>();
        listBuilding = buildingRepository.findAll();
        for(Building b: listBuilding) {
            for (Floor f : b.getFloors()) {
                for (Room r : f.getRooms()) {
                    if (r.getHeating() > value) {
                        list.add(r);
                    }
                }
            }
        }
        return list;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/bySurface")
    public List<Room> getLocalizationBySurface(@RequestParam Float leftValue,Float rightValue) {
        List<Room> list = new ArrayList<>();
        List<Building> listBuilding = new ArrayList<>();
        listBuilding = buildingRepository.findAll();
        for(Building b: listBuilding){
            for(Floor f: b.getFloors()) {
                for (Room r : f.getRooms()) {
                    if (r.getArea() >= leftValue && r.getArea() <= rightValue) {
                        list.add(r);
                    }
                }
            }
        }
        return list;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public Building createBuilding(@RequestBody @Valid BuildingRequest buildingRequest) {

        List<Floor> floors = floorRepository.findAllNotUsed(buildingRequest.getFloorIds());
        if (floors.size() != buildingRequest.getFloorIds().size()) {
            throw new ResourceNotFoundException("There are floors that are not found");
        }
        return buildingRepository.save(new Building(buildingRequest.getName(), floors));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{id}/addFloors")
    public Building addFloors(@RequestBody List<Integer> floorIds, @PathVariable Integer id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));

        List<Floor> floors = floorRepository.findAllNotUsed(floorIds);
        if (floors.size() != floorIds.size()) {
            throw new ResourceNotFoundException("There are floors that are not found");
        }

        building.getFloors().addAll(floors);
        return buildingRepository.save(building);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBuilding(@PathVariable Integer id) {
        return buildingRepository.findById(id).map(building -> {
            buildingRepository.delete(building);
            return ResponseEntity.ok("Deleted building with Id: " + id.toString());
        }).orElseThrow(() -> new ResourceNotFoundException(BUILDING_NOT_FOUND));
    }


}
