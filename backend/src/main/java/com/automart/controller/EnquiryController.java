package com.automart.controller;

import com.automart.dto.EnquiryRequest;
import com.automart.dto.EnquiryResponse;
import com.automart.security.UserPrincipal;
import com.automart.service.EnquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<EnquiryResponse> create(@Valid @RequestBody EnquiryRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        EnquiryResponse response = enquiryService.createEnquiry(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnquiryResponse>> myEnquiries(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(enquiryService.getMyEnquiries(principal.getId()));
    }

    @GetMapping("/seller")
    public ResponseEntity<List<EnquiryResponse>> sellerEnquiries(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(enquiryService.getSellerEnquiries(principal.getId()));
    }
}
