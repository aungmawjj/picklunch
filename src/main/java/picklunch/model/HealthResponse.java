package picklunch.model;


import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class HealthResponse {

    private final String status = "Ok";

    private final ZonedDateTime timestamp = ZonedDateTime.now();

}
