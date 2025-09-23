package golunch.service;

import golunch.exception.GoLunchException;
import golunch.model.CreateLunchPickerRequest;
import golunch.model.PickLunchOptionRequest;
import golunch.model.SubmitLunchOptionRequest;
import golunch.model.entity.LunchPicker;
import golunch.model.entity.User;
import golunch.repository.LunchPickerRepo;
import golunch.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LunchPickerServiceTest {

    @Autowired
    LunchPickerService lunchPickerService;

    @Autowired
    LunchPickerRepo lunchPickerRepo;

    @Autowired
    UserRepo userRepo;

    List<String> usernames = List.of("user1", "user2");

    @BeforeEach
    void setupUsers() {
        userRepo.deleteAll();
        List<User> users = usernames.stream()
                .map(username -> User.builder().username(username).build())
                .toList();
        userRepo.saveAll(users);
    }

    @AfterEach
    void deleteAll() {
        lunchPickerRepo.deleteAll();
    }

    @Test
    void testCanCreateLunchPicker() {
        LunchPicker lunchPicker = createDefaultLunchPicker();

        assertNotNull(lunchPicker);
        assertEquals(LunchPicker.State.SUBMITTING, lunchPicker.getState());
        assertTrue(CollectionUtils.isEmpty(lunchPicker.getLunchOptions()));
        assertNull(lunchPicker.getPickedLunchOption());
    }

    @Test
    void testCannotCreateWhenActiveLunchPickerExists() {
        createDefaultLunchPicker();
        assertThrows(GoLunchException.class, this::createDefaultLunchPicker);
    }

    @Test
    void testCanListLunchPicker() {
        createDefaultLunchPicker();
        Page<LunchPicker> lunchPickers = lunchPickerService.getLunchPickers(PageRequest.of(0, 10));
        assertEquals(1, lunchPickers.getTotalElements());
        assertEquals(1, lunchPickers.getContent().size());
    }

    @Test
    void testCanSubmit() {
        String user0 = usernames.get(0);

        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        assertEquals(LunchPicker.State.SUBMITTING, lunchPicker.getState());
        assertEquals(1, lunchPicker.getLunchOptions().size());
    }

    @Test
    void testAllUserSubmitAndPick() {
        String user0 = usernames.get(0);
        String user1 = usernames.get(1);

        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        PickLunchOptionRequest pickReq = pickRequest(lunchPicker);

        assertThrows(
                GoLunchException.class,
                () -> lunchPickerService.pickLunchOption(pickReq, user0),
                "cannot pick while waiting for other submissions"
        );
        assertEquals(LunchPicker.State.SUBMITTING, lunchPicker.getState());
        assertNull(lunchPicker.getPickedLunchOption());

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopB"), user1);
        assertEquals(LunchPicker.State.READY_TO_PICK, lunchPicker.getState());
        assertEquals(2, lunchPicker.getLunchOptions().size());

        lunchPicker = lunchPickerService.pickLunchOption(pickReq, user0);
        assertEquals(LunchPicker.State.PICKED, lunchPicker.getState());
        assertNotNull(lunchPicker.getPickedLunchOption());
    }

    @Test
    void testSomeUserSubmitAndWaitTimeOverAndPick() throws InterruptedException {
        String user0 = usernames.get(0);
        CreateLunchPickerRequest createReq = new CreateLunchPickerRequest();
        createReq.setWaitTime(Duration.ofMillis(1));

        LunchPicker lunchPicker = lunchPickerService.createLunchPicker(createReq);
        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        Thread.sleep(2); // sleep for wait time over

        lunchPicker = lunchPickerService.pickLunchOption(pickRequest(lunchPicker), user0);
        assertEquals(LunchPicker.State.PICKED, lunchPicker.getState());
        assertNotNull(lunchPicker.getPickedLunchOption());
    }

    @Test
    void testStateUpdateAfterWaitTimeOverWhenHavingSomeOptions() throws InterruptedException {
        String user0 = usernames.get(0);
        CreateLunchPickerRequest createReq = new CreateLunchPickerRequest();
        createReq.setWaitTime(Duration.ofMillis(100));

        LunchPicker lunchPicker = lunchPickerService.createLunchPicker(createReq);
        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);
        assertEquals(LunchPicker.State.SUBMITTING, lunchPicker.getState());

        Thread.sleep(100); // sleep for wait time over

        Page<LunchPicker> lunchPickers = lunchPickerService.getLunchPickers(PageRequest.of(0, 10));
        assertEquals(LunchPicker.State.READY_TO_PICK, lunchPickers.getContent().get(0).getState());
    }

    @Test
    void testCannotSubmitAfterPicked() {
        String user0 = usernames.get(0);
        String user1 = usernames.get(1);

        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);
        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopB"), user1);
        lunchPicker = lunchPickerService.pickLunchOption(pickRequest(lunchPicker), user0);

        assertEquals(LunchPicker.State.PICKED, lunchPicker.getState());
        SubmitLunchOptionRequest submitReq = submitRequest(lunchPicker, "ShopC");

        assertThrows(
                GoLunchException.class,
                () -> lunchPickerService.submitLunchOption(submitReq, user0),
                "cannot submit after picking an option"
        );
    }

    @Test
    void testCanCreateNewLunchPickerAfterPrevOneIsPicked() {
        String user0 = usernames.get(0);
        String user1 = usernames.get(1);

        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);
        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopB"), user1);
        lunchPicker = lunchPickerService.pickLunchOption(pickRequest(lunchPicker), user0);

        assertEquals(LunchPicker.State.PICKED, lunchPicker.getState());

        LunchPicker lunchPicker1 = createDefaultLunchPicker();

        assertNotEquals(lunchPicker.getId(), lunchPicker1.getId());
        assertEquals(LunchPicker.State.SUBMITTING, lunchPicker1.getState());

        Page<LunchPicker> lunchPickers = lunchPickerService.getLunchPickers(PageRequest.of(0, 10));
        assertEquals(2, lunchPickers.getTotalElements());
        assertEquals(2, lunchPickers.getContent().size());
    }

    private SubmitLunchOptionRequest submitRequest(LunchPicker lunchPicker, String shopName) {
        SubmitLunchOptionRequest req = new SubmitLunchOptionRequest();
        req.setLunchPickerId(lunchPicker.getId());
        req.setShopName(shopName);
        return req;
    }

    private PickLunchOptionRequest pickRequest(LunchPicker lunchPicker) {
        PickLunchOptionRequest req = new PickLunchOptionRequest();
        req.setLunchPickerId(lunchPicker.getId());
        return req;
    }

    private LunchPicker createDefaultLunchPicker() {
        return lunchPickerService.createLunchPicker(new CreateLunchPickerRequest());
    }

}