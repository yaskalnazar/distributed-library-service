package com.yaskal.library.mapping;

import com.yaskal.library.model.Book;
import com.yaskal.library.model.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "contributorId", source = "contributor.id")
    BookDto toDto(Book book);

    @Mapping(target = "contributor.id", source = "contributorId")
    Book toEntity(BookDto bookDto);
}

