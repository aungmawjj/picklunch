package picklunch.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Duration;

@Data
public class CreateLunchPickerRequest {

    @Schema(example = "PT10M", description = "Wait time for submitting lunch options")
    private Duration waitTime;

}
