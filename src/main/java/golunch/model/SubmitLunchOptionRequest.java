package golunch.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitLunchOptionRequest {

    @NotNull
    private Long lunchPickerId;

    @NotBlank
    private String shopName;

    private String shopUrl;

}
