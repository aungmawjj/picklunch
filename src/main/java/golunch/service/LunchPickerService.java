package golunch.service;

import golunch.model.CreateLunchPickerRequest;
import golunch.model.PickLunchOptionRequest;
import golunch.model.SubmitLunchOptionRequest;
import golunch.model.entity.LunchPicker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LunchPickerService {

    LunchPicker createLunchPicker(CreateLunchPickerRequest request);

    LunchPicker submitLunchOption(SubmitLunchOptionRequest request, String username);

    LunchPicker pickLunchOption(PickLunchOptionRequest request, String username);

    Page<LunchPicker> getLunchPickers(Pageable pageable);

    LunchPicker getLunchPickerById(Long id);

}
