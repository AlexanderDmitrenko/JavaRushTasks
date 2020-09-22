package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@RestController
@RequestMapping("/rest")
public class ShipController {

    @Autowired
    ShipService shipService;

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship;

        try {
            ship = shipService.getById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (ship == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> saveShip(@RequestBody Ship ship) {

        if (ship == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (ship.getName() == null || ship.getPlanet() == null || ship.getProdDate() == null
                || ship.getSpeed() == null || ship.getCrewSize() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (ship.getName().length() > 50 || ship.getPlanet().length() > 50)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getName().isEmpty() || ship.getPlanet().isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (ship.getUsed() == null) ship.setUsed(false);
        ship.setSpeed((double)(Math.round(ship.getSpeed() * 100)) / 100);
        if (0.1 > ship.getSpeed() || ship.getSpeed() > 0.99) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getProdDate().getTime() < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Calendar date2800 = new GregorianCalendar(2799, 0, 1);
        Calendar date3020 = new GregorianCalendar(3020, 0, 1);
        if (ship.getProdDate().before(date2800.getTime()) || ship.getProdDate().after(date3020.getTime()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ship.initRating();
        this.shipService.save(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


    @RequestMapping(value = "ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable(value = "id") Long id) {

        if (ship == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (id == null || id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!this.shipService.isExist(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        boolean b = shipService.isExist(id);

        if (ship.getName() == null && ship.getPlanet() == null && ship.getShipType() == null && ship.getProdDate() == null
            && ship.getSpeed() == null && ship.getCrewSize() == null && ship.getUsed() == null)
            return new ResponseEntity<>( HttpStatus.OK);


        Ship shipData = this.shipService.getById(id);

        if (ship.getName() != null) {
            if (ship.getName().isEmpty() || ship.getName().length() > 50) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                shipData.setName(ship.getName());

        }


        if (ship.getPlanet() != null) {
            if(ship.getPlanet().length() > 50 || ship.getPlanet().isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipData.setPlanet(ship.getPlanet());
        }

        if (ship.getShipType() != null) shipData.setShipType(ship.getShipType());

        if (ship.getProdDate() != null ) {
            Calendar date2800 = new GregorianCalendar(2799, 0, 1);
            Calendar date3020 = new GregorianCalendar(3020, 0, 1);
            if (ship.getProdDate().before(date2800.getTime()) || ship.getProdDate().after(date3020.getTime()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipData.setProdDate(ship.getProdDate());
        }

        if (ship.getUsed() != null) shipData.setUsed(ship.getUsed());

        if (ship.getSpeed() != null) {
            if (ship.getSpeed() < 0.1 || ship.getSpeed() > 0.99)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipData.setSpeed(ship.getSpeed());
        }

        if (ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipData.setCrewSize(ship.getCrewSize());
        }

        shipData.initRating();

        this.shipService.save(shipData);
        return new ResponseEntity<>(shipData, HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") Long id) {
        if (id == null || id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!this.shipService.isExist(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        this.shipService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public ResponseEntity<List<Ship>> getAllShips(
            @RequestParam(defaultValue = "0", value = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "3", value = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "id", value = "order") String order,
            @RequestParam(defaultValue = "", required = false, value = "name") String name,
            @RequestParam(defaultValue = "", required = false, value = "planet") String planet,
            @RequestParam(defaultValue = "", required = false, value = "shipType") String shipT,
            @RequestParam(required = false, value = "after") Long after,
            @RequestParam(required = false, value = "before") Long before,
            @RequestParam(required = false, value = "isUsed") String isUsedGet,
            @RequestParam(defaultValue = "0", required = false, value = "minSpeed") Double minSpeed,
            @RequestParam(defaultValue = "1", required = false, value = "maxSpeed") Double maxSpeed,
            @RequestParam(defaultValue = "0", required = false, value = "minCrewSize") Integer minCrewSize,
            @RequestParam(defaultValue = "10000", required = false, value = "maxCrewSize") Integer maxCrewSize,
            @RequestParam(defaultValue = "0", required = false, value = "minRating") Double minRating,
            @RequestParam(defaultValue = "81", required = false, value = "maxRating") Double maxRating
    ) {

        ShipOrder shipOrder = ShipOrder.ID;
        if (order.equals("DATE")) shipOrder = ShipOrder.DATE;
        if (order.equals("RATING")) shipOrder = ShipOrder.RATING;
        if (order.equals("SPEED")) shipOrder = ShipOrder.SPEED;

        ShipType shipType = null;
        if (shipT != null) {
            if (shipT.equals("TRANSPORT")) shipType = ShipType.TRANSPORT;
            if (shipT.equals("MERCHANT")) shipType = ShipType.MERCHANT;
            if (shipT.equals("MILITARY")) shipType = ShipType.MILITARY;
        }
        final ShipType shipType1 = shipType;

        boolean isUsed = false;
        if (isUsedGet != null) {
            if (isUsedGet.equals("true")) isUsed = true;
        }
        final boolean isUsedFilter = isUsed;

        Date afterDate = new Date();
        Date beforeDate = new Date();
        if(after == null) {
            afterDate = new GregorianCalendar(2799,00,01).getTime();
        }else {
            afterDate = new Date(after);
        }

        if(before == null) {
            beforeDate = new GregorianCalendar(3020,00,01).getTime();
        }else {
            beforeDate = new Date(before);
        }


        List<Ship> ships = this.shipService.getAll(where(ShipSpecification.nameContains(name).
                        and(ShipSpecification.planetContains(planet).
                                and(ShipSpecification.betweenCrewSize(minCrewSize, maxCrewSize).
                                                and(ShipSpecification.betweenSpeed(minSpeed, maxSpeed).
                                                        and(ShipSpecification.betweenRating(minRating, maxRating)
                                                                .and(ShipSpecification.betweenProdDate(afterDate, beforeDate)
                                                                    .and(ShipSpecification.isUsedContains(isUsedFilter, isUsedGet)
                                                                        .and(ShipSpecification.shipTypeContains(shipType1))))))))),
                PageRequest.of(pageNumber, pageSize, Sort.by(shipOrder.getFieldName())));


        return new ResponseEntity<>(ships, HttpStatus.OK);
    }


    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> getCountShips(
            @RequestParam(defaultValue = "0", value = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "3", value = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "id", value = "order") String order,
            @RequestParam(defaultValue = "", required = false, value = "name") String name,
            @RequestParam(defaultValue = "", required = false, value = "planet") String planet,
            @RequestParam(defaultValue = "", required = false, value = "shipType") String shipT,
            @RequestParam(required = false, value = "after") Long after,
            @RequestParam(required = false, value = "before") Long before,
            @RequestParam(required = false, value = "isUsed") String isUsedGet,
            @RequestParam(defaultValue = "0", required = false, value = "minSpeed") Double minSpeed,
            @RequestParam(defaultValue = "1", required = false, value = "maxSpeed") Double maxSpeed,
            @RequestParam(defaultValue = "0", required = false, value = "minCrewSize") Integer minCrewSize,
            @RequestParam(defaultValue = "10000", required = false, value = "maxCrewSize") Integer maxCrewSize,
            @RequestParam(defaultValue = "0", required = false, value = "minRating") Double minRating,
            @RequestParam(defaultValue = "81", required = false, value = "maxRating") Double maxRating
    ) {

        ShipOrder shipOrder = ShipOrder.ID;
        if (order.equals("DATE")) shipOrder = ShipOrder.DATE;
        if (order.equals("RATING")) shipOrder = ShipOrder.RATING;
        if (order.equals("SPEED")) shipOrder = ShipOrder.SPEED;

        ShipType shipType = null;
        if (shipT != null) {
            if (shipT.equals("TRANSPORT")) shipType = ShipType.TRANSPORT;
            if (shipT.equals("MERCHANT")) shipType = ShipType.MERCHANT;
            if (shipT.equals("MILITARY")) shipType = ShipType.MILITARY;
        }
        final ShipType shipType1 = shipType;

        boolean isUsed = false;
        if (isUsedGet != null) {
            if (isUsedGet.equals("true")) isUsed = true;
        }
        final boolean isUsedFilter = isUsed;

        Date afterDate = new Date();
        Date beforeDate = new Date();
        if(after == null) {
            afterDate = new GregorianCalendar(2799,00,01).getTime();
        }else {
            afterDate = new Date(after);
        }

        if(before == null) {
            beforeDate = new GregorianCalendar(3020,00,01).getTime();
        }else {
            beforeDate = new Date(before);
        }


        List<Ship> ships = this.shipService.getAll(where(ShipSpecification.nameContains(name).
                        and(ShipSpecification.planetContains(planet).
                                and(ShipSpecification.betweenCrewSize(minCrewSize, maxCrewSize).
                                        and(ShipSpecification.betweenSpeed(minSpeed, maxSpeed).
                                                and(ShipSpecification.betweenRating(minRating, maxRating)
                                                        .and(ShipSpecification.betweenProdDate(afterDate, beforeDate)
                                                                .and(ShipSpecification.isUsedContains(isUsedFilter, isUsedGet)
                                                                        .and(ShipSpecification.shipTypeContains(shipType1))))))))));

        Integer count = ships.size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

}
