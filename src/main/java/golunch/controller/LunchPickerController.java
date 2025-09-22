package golunch.controller;

import golunch.model.CreateLunchPickerRequest;
import golunch.model.ErrorResponse;
import golunch.model.PickLunchOptionRequest;
import golunch.model.SubmitLunchOptionRequest;
import golunch.model.entity.LunchPicker;
import golunch.service.LunchPickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lunch-picker")
@Tag(name = "Lunch Picker")

public class LunchPickerController {

    @Autowired
    LunchPickerService lunchPickerService;

    @PostMapping("")
    @Operation(
            summary = "Create a new lunch picker",
            description = "Default wait time is 30 minutes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public LunchPicker createLunchPicker(@RequestBody CreateLunchPickerRequest request) {
        return lunchPickerService.createLunchPicker(request);
    }

    @GetMapping("")
    @Operation(summary = "List lunch pickers")
    public Page<LunchPicker> getLunchPickers(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return lunchPickerService.getLunchPickers(pageable);
    }

    @PostMapping("/option")
    @Operation(
            summary = "Submit lunch option",
            description = "Can submit options before picking one"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public LunchPicker submitLunchOption(
            @RequestBody SubmitLunchOptionRequest request,
            Authentication authentication
    ) {
        return lunchPickerService.submitLunchOption(request, authentication.getName());
    }

    @PostMapping("/pick")
    @Operation(
            summary = "Pick a random lunch option",
            description = "Can pick only once after all users have submitted or wait time is over"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public LunchPicker pickLunchOption(
            @RequestBody PickLunchOptionRequest request,
            Authentication authentication
    ) {
        return lunchPickerService.pickLunchOption(request, authentication.getName());
    }

}
