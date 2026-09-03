package com.automart.repository.spec;

import com.automart.entity.Car;
import com.automart.entity.CarStatus;
import com.automart.entity.FuelType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

// Each static method returns a Specification<Car> that adds ONE predicate
// (or none, if the filter value is null). Specification.where(...).and(...)
// chains them, and JPA/Hibernate translates the whole thing into a single
// dynamic SQL query with only the WHERE clauses that were actually needed.
// This is the clean way to implement "search with any combination of
// optional filters" without N-dimensional method overloading.
public class CarSpecification {

    public static Specification<Car> hasStatus(CarStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, cb) ->
                (brand == null || brand.isBlank())
                        ? null
                        : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Car> hasFuelType(FuelType fuelType) {
        return (root, query, cb) ->
                fuelType == null ? null : cb.equal(root.get("fuelType"), fuelType);
    }

    public static Specification<Car> hasLocation(String location) {
        return (root, query, cb) ->
                (location == null || location.isBlank())
                        ? null
                        : cb.equal(cb.lower(root.get("location")), location.toLowerCase());
    }

    public static Specification<Car> hasYear(Integer year) {
        return (root, query, cb) ->
                year == null ? null : cb.equal(root.get("year"), year);
    }

    public static Specification<Car> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<Car> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("brand")), like),
                    cb.like(cb.lower(root.get("model")), like)
            );
        };
    }
}
