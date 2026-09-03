package com.automart.repository;

import com.automart.entity.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    List<Enquiry> findByBuyerId(Long buyerId);
    @org.springframework.data.jpa.repository.Query(
        "SELECT e FROM Enquiry e WHERE e.car.seller.id = :sellerId")
    List<Enquiry> findBySellerId(Long sellerId);
}
