package com.automart.dto;

import com.automart.entity.EnquiryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnquiryResponse {
    private Long id;
    private Long carId;
    private String carTitle; // "brand model year"
    private Long buyerId;
    private String buyerName;
    private String message;
    private EnquiryStatus status;
    private LocalDateTime createdAt;
}
