package picklunch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import picklunch.model.CreateLunchPickerRequest;
import picklunch.model.PickLunchOptionRequest;
import picklunch.model.SubmitLunchOptionRequest;
import picklunch.model.entity.LunchPicker;
import picklunch.service.LunchPickerService;

@RestController
@RequestMapping("/api/lunch-picker")
@Tag(name = "Lunch Picker")
public class LunchPickerController {

    @Autowired
    LunchPickerService lunchPickerService;

    @GetMapping("")
    @Operation(summary = "List lunch pickers")
    public Page<LunchPicker> getLunchPickers(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return lunchPickerService.getLunchPickers(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lunch picker by id")
    public LunchPicker getLunchPickers(@PathVariable Long id) {
        return lunchPickerService.getLunchPickerById(id);
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new lunch picker",
            description = "Default wait time is 30 minutes"
    )
    public LunchPicker createLunchPicker(@Validated @RequestBody CreateLunchPickerRequest request) {
        return lunchPickerService.createLunchPicker(request);
    }

    @PostMapping("/option")
    @Operation(
            summary = "Submit lunch option",
            description = "Can submit options before picking one"
    )
    public LunchPicker submitLunchOption(
            @Validated @RequestBody SubmitLunchOptionRequest request,
            Authentication authentication
    ) {
        return lunchPickerService.submitLunchOption(request, authentication.getName());
    }

    @PostMapping("/pick")
    @Operation(
            summary = "Pick a random lunch option",
            description = "Can pick only once after all users have submitted or wait time is over"
    )
    public LunchPicker pickLunchOption(
            @Validated @RequestBody PickLunchOptionRequest request,
            Authentication authentication
    ) {
        return lunchPickerService.pickLunchOption(request, authentication.getName());
    }

}
