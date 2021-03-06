package com.io.buildings.controllers;

import com.io.buildings.models.Localization;
import com.io.buildings.models.Room;
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
@RequestMapping("/rooms")
public class RoomController {

    private static final String ROOM_NOT_FOUND = "Room with given Id doesn't exists";
    private final RoomRepository roomRepository;

    @Autowired
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public Room getInfo(@PathVariable Integer id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/surface")
    public Float getSurface(@PathVariable Integer id) {
        return roomRepository.findById(id).map(Localization::countSurface
        ).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/cube")
    public Float getCube(@PathVariable Integer id) {
        return roomRepository.findById(id).map(Localization::countCube
        ).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/light")
    public Float getAverageLight(@PathVariable Integer id) {
        return roomRepository.findById(id).map(Localization::countAverageLight
        ).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/heating")
    public Float getAverageHeating(@PathVariable Integer id) {
        return roomRepository.findById(id).map(Localization::countAverageHeating
        ).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/aboveHeating")
    public List<Room> getLocalizationAboveHeating(@RequestParam Float value) {
        List<Room> list = new ArrayList<>();
        List<Room> response = new ArrayList<>();
        list = roomRepository.findAll();
        for (Room r: list){
            if(r.getHeating() >= value){
                response.add(r);
            }
        }
        return response;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/bySurface")
    public List<Room> getLocalizationBySurface(@RequestParam Float leftValue,Float rightValue) {
        List<Room> list = new ArrayList<>();
        List<Room> response = new ArrayList<>();
        list = roomRepository.findAll();
        for (Room r: list){
            if(r.getArea() >= leftValue && r.getArea() <= rightValue){
                response.add(r);
            }
        }
        return response;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public Room createRoom(@RequestBody @Valid RoomRequest roomRequest) {
        return roomRepository.save(new Room(roomRequest.getName(), roomRequest.getArea(), roomRequest.getCube(),
                roomRequest
                .getHeating(), roomRequest.getLight()));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/{id}")
    public Room editRoom(@RequestBody RoomRequest roomRequest, @PathVariable Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));

        if (roomRequest.getArea() != null) room.setArea(roomRequest.getArea());
        if (roomRequest.getCube() != null) room.setCube(roomRequest.getCube());
        if (roomRequest.getHeating() != null) room.setHeating(roomRequest.getHeating());
        if (roomRequest.getLight() != null) room.setLight(roomRequest.getLight());
        if (roomRequest.getName() != null) room.setName(roomRequest.getName());

        return roomRepository.save(room);
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteRoom(@PathVariable Integer id) {
        return roomRepository.findById(id).map(room -> {
            roomRepository.delete(room);
            return ResponseEntity.ok("Deleted room with Id: " + id.toString());
        }).orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND));
    }

}
