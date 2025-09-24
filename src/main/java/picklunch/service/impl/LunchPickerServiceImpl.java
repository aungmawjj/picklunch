package picklunch.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import picklunch.exception.PickLunchException;
import picklunch.model.CreateLunchPickerRequest;
import picklunch.model.PickLunchOptionRequest;
import picklunch.model.SubmitLunchOptionRequest;
import picklunch.model.entity.LunchOption;
import picklunch.model.entity.LunchPicker;
import picklunch.repository.LunchPickerRepo;
import picklunch.repository.UserRepo;
import picklunch.service.LunchPickerService;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
@Service
public class LunchPickerServiceImpl implements LunchPickerService {

    @Autowired
    LunchPickerRepo lunchPickerRepo;

    @Autowired
    UserRepo userRepo;

    @Value("${picklunch.picker.wait-time}")
    Duration defaultWaitTime;

    @Override
    @Transactional(readOnly = true)
    public Page<LunchPicker> getLunchPickers(Pageable pageable) {
        Page<LunchPicker> lunchPickers = lunchPickerRepo.findAll(pageable);
        // for the ui to display the state and conditional actions
        // update state only for the response, no write action to database
        lunchPickers.forEach(this::updateStateIfWaitTimeOverWithSomeOptions);
        return lunchPickers;
    }

    @Override
    @Transactional(readOnly = true)
    public LunchPicker getLunchPickerById(Long id) {
        LunchPicker lunchPicker = getLunchPickerByIdOrElseThrow(id);
        this.updateStateIfWaitTimeOverWithSomeOptions(lunchPicker);
        return lunchPicker;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker createLunchPicker(CreateLunchPickerRequest request) {
        log.info("Creating lunch picker");

        // for simplicity, not allow creating when there is a picker with not PICKED state
        validateNoActiveLunchPicker();

        Duration waitTime = ObjectUtils.getIfNull(request.getWaitTime(), defaultWaitTime);
        LunchPicker lunchPicker = LunchPicker.builder()
                .state(LunchPicker.State.SUBMITTING)
                .startTime(ZonedDateTime.now())
                .waitTime(waitTime)
                .build();
        lunchPicker = lunchPickerRepo.save(lunchPicker);

        log.info("Created lunch picker, id={} duration={} state={}",
                lunchPicker.getId(), lunchPicker.getWaitTime(), lunchPicker.getState());
        return lunchPicker;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker submitLunchOption(SubmitLunchOptionRequest request, String username) {
        log.info("Submitting lunch option");
        LunchPicker lunchPicker = getLunchPickerByIdOrElseThrow(request.getLunchPickerId());

        validateNotPicked(lunchPicker);
        validateNotSubmitted(lunchPicker, username);

        LunchOption lunchOption = createLunchOption(request, username);
        if (CollectionUtils.isEmpty(lunchPicker.getLunchOptions())) {
            lunchPicker.setLunchOptions(new ArrayList<>());
            lunchPicker.setFirstSubmittedUsername(username);
        }
        lunchPicker.getLunchOptions().add(lunchOption);

        if (isWaitTimeOver(lunchPicker) || isAllSubmitted(lunchPicker)) {
            lunchPicker.setState(LunchPicker.State.READY_TO_PICK);
        }
        lunchPicker = lunchPickerRepo.save(lunchPicker);
        log.info("Submitted lunch option, picker_state={}", lunchPicker.getState());
        return lunchPicker;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker pickLunchOption(PickLunchOptionRequest request, String username) {
        log.info("Picking lunch option");
        LunchPicker lunchPicker = getLunchPickerByIdOrElseThrow(request.getLunchPickerId());

        updateStateIfWaitTimeOverWithSomeOptions(lunchPicker);

        validateNotPicked(lunchPicker); // redundant validator, to get better error message
        validateReadyToPick(lunchPicker);
        validateHasOptionsToPick(lunchPicker);
        validatePickerIsFirstSubmitter(lunchPicker, username);

        int index = new Random().nextInt(lunchPicker.getLunchOptions().size());
        lunchPicker.setPickedLunchOption(lunchPicker.getLunchOptions().get(index));
        lunchPicker.setState(LunchPicker.State.PICKED);

        lunchPicker = lunchPickerRepo.save(lunchPicker);

        LunchOption picked = lunchPicker.getPickedLunchOption();
        log.info("Picked lunch option, shopName={} picker_id={} picker_state={}",
                picked.getShopName(), lunchPicker.getId(), lunchPicker.getState());
        return lunchPicker;
    }

    private void validateNoActiveLunchPicker() {
        long activeCount = lunchPickerRepo.countByStateNot(LunchPicker.State.PICKED);
        if (activeCount > 0) {
            throw new PickLunchException("Active lunch picker exists");
        }
    }

    private void validateNotPicked(LunchPicker lunchPicker) {
        if (LunchPicker.State.PICKED.equals(lunchPicker.getState())) {
            throw new PickLunchException("Already picked a lunch option");
        }
    }

    private void validateNotSubmitted(LunchPicker lunchPicker, String username) {
        if (CollectionUtils.isEmpty(lunchPicker.getLunchOptions())) {
            return;
        }
        boolean submitted = lunchPicker.getLunchOptions().stream().anyMatch(option ->
                option.getSubmittedUsername().equals(username));
        if (submitted) {
            throw new PickLunchException("Already submitted a lunch option");
        }
    }

    private void validateReadyToPick(LunchPicker lunchPicker) {
        if (!LunchPicker.State.READY_TO_PICK.equals(lunchPicker.getState())) {
            throw new PickLunchException("Waiting for other submissions");
        }
    }

    private void validateHasOptionsToPick(LunchPicker lunchPicker) {
        if (CollectionUtils.isEmpty(lunchPicker.getLunchOptions())) {
            throw new PickLunchException("No lunch option to pick");
        }
    }

    private void validatePickerIsFirstSubmitter(LunchPicker lunchPicker, String username) {
        if (!username.equals(lunchPicker.getFirstSubmittedUsername())) {
            throw new PickLunchException("Only first submitter can pick a random option");
        }
    }

    private LunchOption createLunchOption(SubmitLunchOptionRequest request, String username) {
        return LunchOption.builder()
                .submittedUsername(username)
                .submitter(userRepo.findByUsername(username))
                .shopName(request.getShopName())
                .shopUrl(request.getShopUrl())
                .build();
    }

    private LunchPicker getLunchPickerByIdOrElseThrow(Long id) {
        return lunchPickerRepo.findById(id)
                .orElseThrow(() -> new PickLunchException("Lunch picker not found"));
    }

    private void updateStateIfWaitTimeOverWithSomeOptions(LunchPicker lunchPicker) {
        if (isStateSubmitting(lunchPicker) && isWaitTimeOver(lunchPicker) && hasOptions(lunchPicker)) {
            lunchPicker.setState(LunchPicker.State.READY_TO_PICK);
        }
    }

    private boolean isStateSubmitting(LunchPicker lunchPicker) {
        return LunchPicker.State.SUBMITTING.equals(lunchPicker.getState());
    }

    private boolean isWaitTimeOver(LunchPicker lunchPicker) {
        ZonedDateTime waitTimeEnd = lunchPicker.getStartTime().plus(lunchPicker.getWaitTime());
        return ZonedDateTime.now().isAfter(waitTimeEnd);
    }

    private boolean isAllSubmitted(LunchPicker lunchPicker) {
        return lunchPicker.getLunchOptions().size() == userRepo.count();
    }

    private boolean hasOptions(LunchPicker lunchPicker) {
        return !CollectionUtils.isEmpty(lunchPicker.getLunchOptions());
    }
}
