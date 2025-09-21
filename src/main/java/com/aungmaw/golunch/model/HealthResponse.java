package com.aungmaw.golunch.model;


import lombok.Getter;

import java.util.Date;

@Getter
public class HealthResponse {
    private final String status = "Ok";
    private final Date timestamp = new Date();
}
