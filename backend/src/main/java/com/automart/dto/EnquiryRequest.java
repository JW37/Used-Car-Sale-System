package com.automart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EnquiryRequest {

    @NotNull
    private Long carId;

    @NotBlank
    private String message;
}
