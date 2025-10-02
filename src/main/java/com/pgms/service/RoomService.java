package com.pgms.service;

import com.pgms.domain.Bed;
import com.pgms.domain.Room;
import com.pgms.dto.BedDtos;
import com.pgms.dto.RoomDtos;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.BedRepo;
import com.pgms.repo.RoomRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.util.Enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepo roomRepo;
    private final BedRepo bedRepo;
    private final TenantRepo tenantRepo;

    public RoomService(RoomRepo roomRepo, BedRepo bedRepo, TenantRepo tenantRepo) {
        this.roomRepo = roomRepo;
        this.bedRepo = bedRepo;
        this.tenantRepo = tenantRepo;
    }

    @Transactional
    public UUID create(RoomDtos.CreateRequest req) {
        if (roomRepo.existsByNumber(req.number)) {
            throw new BadRequestException("Room number already exists");
        }

        Room room = new Room();
        room.setNumber(req.number);
        room.setFloorNumber(req.floorNumber);
        room.setCapacity(req.capacity);
        room.setBaseRent(req.baseRent);
        room.setStatus(Enums.RoomStatus.AVAILABLE);
        room = roomRepo.save(room);

        for (int i = 1; i <= req.capacity; i++) {
            Bed b = new Bed();
            b.setRoom(room);
            b.setIndex(i);
            b.setStatus(Enums.BedStatus.AVAILABLE);
            b.setCode(req.number + bedSuffix(i)); // 101A, 101B...
            bedRepo.save(b);
        }

        log.info("Created room {} with {} beds", room.getNumber(), req.capacity);
        return room.getId();
    }

    @Transactional
    public void update(RoomDtos.UpdateRequest req) {
        Room room = roomRepo.findById(req.id)
                .orElseThrow(() -> new NotFoundException("Room not found: " + req.id));

        if (!room.getNumber().equals(req.number)) {
            if (roomRepo.existsByNumber(req.number)) {
                throw new BadRequestException("Room number already exists");
            }
            room.setNumber(req.number);
            for (Bed b : room.getBeds()) {
                b.setCode(req.number + bedSuffix(b.getIndex()));
                bedRepo.save(b);
            }
        }

        int oldCap = room.getCapacity();
        int newCap = req.capacity;

        room.setFloorNumber(req.floorNumber);
        room.setCapacity(newCap);
        room.setBaseRent(req.baseRent);
        if (req.status != null) room.setStatus(req.status);

        if (newCap > oldCap) {
            for (int i = oldCap + 1; i <= newCap; i++) {
                if (bedRepo.existsByRoom_IdAndIndex(room.getId(), i)) continue;
                Bed b = new Bed();
                b.setRoom(room);
                b.setIndex(i);
                b.setStatus(Enums.BedStatus.AVAILABLE);
                b.setCode(room.getNumber() + bedSuffix(i));
                bedRepo.save(b);
            }
        } else if (newCap < oldCap) {
            for (int i = oldCap; i > newCap; i--) {
                Bed b = bedRepo.findByRoom_IdAndIndex(room.getId(), i)
                        .orElseThrow(() -> new NotFoundException("Bed index missing during shrink"));
                if (tenantRepo.existsByBed_Id(b.getId())) {
                    throw new BadRequestException("Cannot reduce capacity. Bed " + i + " is occupied");
                }
                bedRepo.delete(b);
            }
        }

        roomRepo.save(room);
        log.info("Updated room {} capacity {}->{}", room.getNumber(), oldCap, newCap);
    }

    @Transactional
    public void delete(UUID roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

        for (Bed b : room.getBeds()) {
            if (tenantRepo.existsByBed_Id(b.getId())) {
                throw new BadRequestException("Cannot delete room; bed " + b.getIndex() + " is occupied");
            }
        }
        roomRepo.delete(room);
        log.info("Deleted room {}", room.getNumber());
    }

    @Transactional(readOnly = true)
    public RoomDtos.Response get(UUID id) {
        Room r = roomRepo.findById(id).orElseThrow(() -> new NotFoundException("Room not found: " + id));
        return toResponse(r);
    }

    @Transactional(readOnly = true)
    public java.util.List<BedDtos.Response> listBeds(UUID roomId) {
        Room r = roomRepo.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found"));
        return r.getBeds().stream().map(this::toBedResponse).collect(Collectors.toList());
    }

    private RoomDtos.Response toResponse(Room r) {
        RoomDtos.Response res = new RoomDtos.Response();
        res.id = r.getId();
        res.number = r.getNumber();
        res.floorNumber = r.getFloorNumber();
        res.capacity = r.getCapacity();
        res.baseRent = r.getBaseRent();
        res.status = r.getStatus();
        return res;
    }

    private BedDtos.Response toBedResponse(Bed b) {
        BedDtos.Response res = new BedDtos.Response();
        res.id = b.getId();
        res.roomId = b.getRoom().getId();
        res.index = b.getIndex();
        res.status = b.getStatus();
        res.code = b.getCode();
        res.priceOverride = b.getPriceOverride();
        return res;
    }

    private String bedSuffix(int index) {
        int base = 'A' + (index - 1);
        return String.valueOf((char) base);
    }
}
