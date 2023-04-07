package com.yaskal.library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
}

