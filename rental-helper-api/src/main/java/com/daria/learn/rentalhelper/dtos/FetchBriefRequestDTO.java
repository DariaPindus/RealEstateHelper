package com.daria.learn.rentalhelper.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@NoArgsConstructor
public class FetchBriefRequestDTO implements Serializable {
    private static final long serialVersionUID = 1535259511471122655L;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Getter
    private ZonedDateTime dateTime = ZonedDateTime.now();
    @Getter @Setter
    private String dataSource;

    public FetchBriefRequestDTO(String dataSource) {
        this.dataSource = dataSource;
    }
}
