package com.yaskal.library.mapping;

import com.yaskal.library.model.Book;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.Reservation;
import com.yaskal.library.model.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    ReservationDto toDto(Reservation reservation);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "book.id", source = "bookId")
    Reservation toEntity(ReservationDto reservationDto);
}

