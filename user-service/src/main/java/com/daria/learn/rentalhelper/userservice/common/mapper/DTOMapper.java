package com.daria.learn.rentalhelper.userservice.common.mapper;

public interface DTOMapper<DTO, Entity> {

    Entity fromDTO(DTO dto);

    DTO toDTO(Entity entity);

}
