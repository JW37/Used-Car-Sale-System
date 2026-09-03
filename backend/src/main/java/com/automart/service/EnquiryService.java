package com.automart.service;

import com.automart.dto.EnquiryRequest;
import com.automart.dto.EnquiryResponse;
import com.automart.entity.Car;
import com.automart.entity.Enquiry;
import com.automart.entity.User;
import com.automart.exception.ResourceNotFoundException;
import com.automart.repository.CarRepository;
import com.automart.repository.EnquiryRepository;
import com.automart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnquiryResponse createEnquiry(EnquiryRequest request, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        Enquiry enquiry = Enquiry.builder()
                .buyer(buyer)
                .car(car)
                .message(request.getMessage())
                .build();

        return toResponse(enquiryRepository.save(enquiry));
    }

    public List<EnquiryResponse> getMyEnquiries(Long buyerId) {
        return enquiryRepository.findByBuyerId(buyerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // Enquiries the seller has received across all their listed cars.
    public List<EnquiryResponse> getSellerEnquiries(Long sellerId) {
        return enquiryRepository.findBySellerId(sellerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private EnquiryResponse toResponse(Enquiry e) {
        return EnquiryResponse.builder()
                .id(e.getId())
                .carId(e.getCar().getId())
                .carTitle(e.getCar().getBrand() + " " + e.getCar().getModel() + " " + e.getCar().getYear())
                .buyerId(e.getBuyer().getId())
                .buyerName(e.getBuyer().getName())
                .message(e.getMessage())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
