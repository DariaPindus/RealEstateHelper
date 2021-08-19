package com.daria.learn.rentalhelper.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
public class FetchDetailRequestDTO implements Serializable {
    private static final long serialVersionUID = 6116963974697695529L;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Getter
    private ZonedDateTime dateTime = ZonedDateTime.now();
    @Getter @Setter
    private String dataSource;
    @Getter @Setter
    private List<String> urls;

    public FetchDetailRequestDTO(String dataSource, List<String> urls) {
        this.dataSource = dataSource;
        this.urls = urls;
    }

}
