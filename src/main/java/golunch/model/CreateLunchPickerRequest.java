package golunch.model;

import lombok.Data;

import java.time.Duration;

@Data
public class CreateLunchPickerRequest {

    private Duration waitTime;

}
