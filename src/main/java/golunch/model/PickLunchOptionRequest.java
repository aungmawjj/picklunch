package golunch.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PickLunchOptionRequest {

    @NotNull
    private Long lunchPickerId;

}
