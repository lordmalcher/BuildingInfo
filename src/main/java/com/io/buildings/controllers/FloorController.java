package com.io.buildings.controllers;

import com.io.buildings.models.Building;
import com.io.buildings.models.Floor;
import com.io.buildings.models.Localization;
import com.io.buildings.models.Room;
import com.io.buildings.repositories.FloorRepository;
import com.io.buildings.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.io.buildings.controllers.requests.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/floors")
public class FloorController {

    private static final String FLOOR_NOT_FOUND = "Floor with given Id doesn't exists";
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public FloorController(FloorRepository floorRepository, RoomRepository roomRepository) {
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public Floor getInfo(@PathVariable Integer id) {
        return floorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/surface")
    public Float getSurface(@PathVariable Integer id) {
        return floorRepository.findById(id).map(Localization::countSurface
        ).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/cube")
    public Float getCube(@PathVariable Integer id) {
        return floorRepository.findById(id).map(Localization::countCube
        ).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/light")
    public Float getAverageLight(@PathVariable Integer id) {
        return floorRepository.findById(id).map(Localization::countAverageLight
        ).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/heating")
    public Float getAverageHeating(@PathVariable Integer id) {
        return floorRepository.findById(id).map(Localization::countAverageHeating
        ).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/aboveHeating")
    public List<Room> getLocalizationAboveHeating(@RequestParam Float value) {
        List<Room> list = new ArrayList<>();
        List<Floor> listFloor = new ArrayList<>();
        listFloor = floorRepository.findAll();
        for(Floor f: listFloor){
            for (Room r: f.getRooms()){
                if(r.getHeating() > value){
                    list.add(r);
                }
            }
        }
        return list;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/bySurface")
    public List<Room> getLocalizationBySurface(@RequestParam Float leftValue,Float rightValue) {
        List<Room> list = new ArrayList<>();
        List<Floor> listFloor = new ArrayList<>();
        listFloor = floorRepository.findAll();
        for(Floor f: listFloor) {
            for (Room r : f.getRooms()) {
                if (r.getArea() >= leftValue && r.getArea() <= rightValue) {
                    list.add(r);
                }
            }
        }
        return list;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public Floor createFloor(@RequestBody @Valid FloorRequest floorRequest) {
        List<Room> rooms = roomRepository.findAllNotUsed(floorRequest.getRoomIds());
        if (rooms.size() != floorRequest.getRoomIds().size()) {
            throw new ResourceNotFoundException("There are rooms that are not found");
        }
        return floorRepository.save(new Floor(floorRequest.getName(), rooms));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{id}/addRooms")
    public Floor addRooms(@RequestBody List<Integer> roomIds, @PathVariable Integer id) {
        Floor floor = floorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
        List<Room> rooms = roomRepository.findAllNotUsed(roomIds);
        if (rooms.size() != roomIds.size()) {
            throw new ResourceNotFoundException("There are rooms that are not found");
        }
        floor.getRooms().addAll(rooms);
        return floorRepository.save(floor);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFloor(@PathVariable Integer id) {
        return floorRepository.findById(id).map(floor -> {
            floorRepository.delete(floor);
            return ResponseEntity.ok("Deleted floor with Id: " + id.toString());
        }).orElseThrow(() -> new ResourceNotFoundException(FLOOR_NOT_FOUND));
    }

}
