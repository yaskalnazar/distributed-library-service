package com.yaskal.library.mapping;

import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}

