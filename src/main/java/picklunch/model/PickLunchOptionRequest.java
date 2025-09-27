package picklunch.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PickLunchOptionRequest {

    @NotNull
    @Schema(example = "1", description = "Lunch Picker Id")
    private Long lunchPickerId;

}
