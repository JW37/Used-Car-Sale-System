package com.automart.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiError {
    private int status;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
}
