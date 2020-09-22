package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    Ship getById(Long id);

    void save(Ship ship);

    void delete(Long id);

    boolean isExist(Long id);

    List<Ship> getAll();

    List<Ship> getAll(Pageable pageable);

    List<Ship> getAll(Specification<Ship> spec);

    List<Ship> getAll(Specification<Ship> shipSpecification, Pageable pageable);


}
