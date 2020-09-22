package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class ShipSpecification {
    public static Specification<Ship> nameContains(String name){
        return (Specification<Ship>) (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Ship> planetContains(String planet){
        return (Specification<Ship>) (root, query, cb) -> cb.like(root.get("planet"), "%" + planet + "%");
    }

    public static Specification<Ship> betweenCrewSize(Integer minCrewSize, Integer maxCrewSize){
        return (Specification<Ship>) (root, query, cb) -> cb.between(root.get("crewSize"), minCrewSize, maxCrewSize );
    }

    public static Specification<Ship> betweenSpeed(Double minSpeed, Double maxSpeed){
        return (Specification<Ship>) (root, query, cb) -> cb.between(root.get("speed"), minSpeed, maxSpeed );
    }

    public static Specification<Ship> betweenRating(Double minRating, Double maxRating){
        return (Specification<Ship>) (root, query, cb) -> cb.between(root.get("rating"), minRating, maxRating );
    }

    public static Specification<Ship> shipTypeContains(ShipType shipType){
        if (shipType == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return null;
                }
            };
        }else {
            return (Specification<Ship>) (root, query, cb) -> cb.equal(root.get("shipType"), shipType);
        }

    }

    public static Specification<Ship> isUsedContains(boolean isUsed, String isUsedGet){
        if (isUsedGet == null) {
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return null;
                }
            };
        }else {
            return (Specification<Ship>) (root, query, cb) -> cb.equal(root.get("isUsed"), isUsed);
        }
    }

    public static Specification<Ship> betweenProdDate(Date after, Date before){
        return (Specification<Ship>) (root, query, cb) -> cb.between(root.get("prodDate"), after, before );
    }


}
