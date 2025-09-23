package picklunch.service;

import picklunch.model.CreateLunchPickerRequest;
import picklunch.model.PickLunchOptionRequest;
import picklunch.model.SubmitLunchOptionRequest;
import picklunch.model.entity.LunchPicker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LunchPickerService {

    LunchPicker createLunchPicker(CreateLunchPickerRequest request);

    LunchPicker submitLunchOption(SubmitLunchOptionRequest request, String username);

    LunchPicker pickLunchOption(PickLunchOptionRequest request, String username);

    Page<LunchPicker> getLunchPickers(Pageable pageable);

    LunchPicker getLunchPickerById(Long id);

}
