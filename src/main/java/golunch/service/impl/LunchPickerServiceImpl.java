package golunch.service.impl;

import golunch.exception.GoLunchException;
import golunch.model.CreateLunchPickerRequest;
import golunch.model.PickLunchOptionRequest;
import golunch.model.SubmitLunchOptionRequest;
import golunch.model.entity.LunchOption;
import golunch.model.entity.LunchPicker;
import golunch.repository.LunchPickerRepo;
import golunch.repository.UserRepo;
import golunch.service.LunchPickerService;
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

    @Value("${golunch.picker.wait-time}")
    Duration defaultWaitTime;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker createLunchPicker(CreateLunchPickerRequest request) {
        log.info("Creating lunch picker");
        long activeCount = lunchPickerRepo.countByStateNot(LunchPicker.State.PICKED);
        if (activeCount > 0) {
            // for now, allow only one active lunch picker
            throw new GoLunchException("Active lunch picker exists");
        }

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
    @Transactional(readOnly = true)
    public Page<LunchPicker> getLunchPickers(Pageable pageable) {
        Page<LunchPicker> lunchPickers = lunchPickerRepo.findAll(pageable);
        lunchPickers.forEach(this::updateStateIfWaitTimeOverWithSomeOptions);
        return lunchPickers;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker submitLunchOption(SubmitLunchOptionRequest request, String username) {
        log.info("Submitting lunch option");
        LunchPicker lunchPicker = getLunchPickerById(request.getLunchPickerId());

        ensureNotPicked(lunchPicker);

        LunchOption lunchOption = getOrAddLunchOption(lunchPicker, username);
        lunchOption.setUsername(username);
        lunchOption.setShopName(request.getShopName());
        lunchOption.setShopUrl(request.getShopUrl());

        if (isWaitTimeOver(lunchPicker) || isAllSubmitted(lunchPicker)) {
            lunchPicker.setState(LunchPicker.State.READY_TO_PICK);
        }

        lunchPicker = lunchPickerRepo.save(lunchPicker);

        log.info("Submitted lunch option, picker_state={}", lunchPicker.getState());
        return lunchPicker;
    }

    private void ensureNotPicked(LunchPicker lunchPicker) {
        if (LunchPicker.State.PICKED.equals(lunchPicker.getState())) {
            throw new GoLunchException("Already picked a lunch option");
        }
    }

    private LunchPicker getLunchPickerById(Long id) {
        return lunchPickerRepo.findById(id)
                .orElseThrow(() -> new GoLunchException("Lunch picker not found"));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LunchPicker pickLunchOption(PickLunchOptionRequest request, String username) {
        log.info("Picking lunch option");
        LunchPicker lunchPicker = getLunchPickerById(request.getLunchPickerId());

        ensureNotPicked(lunchPicker);
        updateStateIfWaitTimeOverWithSomeOptions(lunchPicker);

        if (!LunchPicker.State.READY_TO_PICK.equals(lunchPicker.getState())) {
            throw new GoLunchException("Waiting for other submissions");
        }
        if (CollectionUtils.isEmpty(lunchPicker.getLunchOptions())) {
            throw new GoLunchException("No lunch option to pick");
        }

        int index = new Random().nextInt(lunchPicker.getLunchOptions().size());
        lunchPicker.setPickedLunchOption(lunchPicker.getLunchOptions().get(index));
        lunchPicker.setState(LunchPicker.State.PICKED);

        lunchPicker = lunchPickerRepo.save(lunchPicker);

        LunchOption picked = lunchPicker.getPickedLunchOption();
        log.info("Picked lunch option, shopName={} picker_id={} picker_state={}",
                picked.getShopName(), lunchPicker.getId(), lunchPicker.getState());
        return lunchPicker;
    }

    private LunchOption getOrAddLunchOption(LunchPicker lunchPicker, String username) {
        if (lunchPicker.getLunchOptions() == null) {
            lunchPicker.setLunchOptions(new ArrayList<>());
        }
        LunchOption lunchOption = lunchPicker.getLunchOptions().stream()
                .filter(option -> username.equals(option.getUsername()))
                .findFirst().orElse(new LunchOption());

        if (lunchOption.getId() == null) {
            lunchPicker.getLunchOptions().add(lunchOption);
        }
        return lunchOption;
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
