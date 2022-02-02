package com.daria.learn.rentalhelper.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class TelegramUserDTO extends BriefUserDTO {

    @Getter
    @Setter
    public String userId;

}
