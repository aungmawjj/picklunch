package picklunch.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitLunchOptionRequest {

    @NotNull
    @Schema(name = "Lunch Picker Id", example = "1")
    private Long lunchPickerId;

    @NotBlank
    @Schema(example = "Pizza Hut", description = "Shop/Restaurant Name")
    private String shopName;

    @Schema(example = "https://www.google.com/maps/...", description = "Shop/Restaurant URL")
    private String shopUrl;

}
