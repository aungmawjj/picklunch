package picklunch.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import picklunch.exception.PickLunchException;
import picklunch.model.CreateLunchPickerRequest;
import picklunch.model.PickLunchOptionRequest;
import picklunch.model.SubmitLunchOptionRequest;
import picklunch.model.entity.LunchPicker;
import picklunch.model.entity.User;
import picklunch.repository.LunchPickerRepo;
import picklunch.repository.UserRepo;

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
        assertThrows(PickLunchException.class, this::createDefaultLunchPicker);
    }

    @Test
    void testCanGetLunchPickerById() {
        LunchPicker lunchPicker = createDefaultLunchPicker();
        LunchPicker lunchPicker1 = lunchPickerService.getLunchPickerById(lunchPicker.getId());
        assertEquals(lunchPicker.getId(), lunchPicker1.getId());
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
    void testResponseIncludeSubmittedUser() {
        String user0 = usernames.get(0);
        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        assertNotNull(lunchPicker.getFirstSubmitter());
        assertNotNull(lunchPicker.getLunchOptions().get(0).getSubmitter());

        assertNotNull(user0, lunchPicker.getFirstSubmitter().getUsername());
        assertNotNull(user0, lunchPicker.getLunchOptions().get(0).getSubmitter().getUsername());
    }

    @Test
    void testGetNotFailedForNotFoundUser() {
        // when users are removed

        LunchPicker lunchPicker = createDefaultLunchPicker();

        String username = "not_found_username";
        lunchPicker = lunchPickerService.submitLunchOption(
                submitRequest(lunchPicker, "ShopA"), username);

        assertNull(lunchPicker.getFirstSubmitter());
        assertNull(lunchPicker.getLunchOptions().get(0).getSubmitter());

        assertEquals(username, lunchPicker.getFirstSubmittedUsername());
        assertEquals(username, lunchPicker.getLunchOptions().get(0).getSubmittedUsername());
    }


    @Test
    void testCannotSubmitMoreThanOnce() {
        String user0 = usernames.get(0);
        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);
        assertThrows(PickLunchException.class, () ->
                lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopB"), user0));
    }

    @Test
    void testAllUserSubmitAndPick() {
        String user0 = usernames.get(0);
        String user1 = usernames.get(1);

        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        PickLunchOptionRequest pickReq = pickRequest(lunchPicker);

        assertThrows(
                PickLunchException.class,
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

        lunchPicker = lunchPickerService.getLunchPickerById(lunchPicker.getId());
        assertEquals(LunchPicker.State.READY_TO_PICK, lunchPicker.getState());
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
                PickLunchException.class,
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

    @Test
    void testOnlyFirstSubmitterCanPick() {
        String user0 = usernames.get(0);
        String user1 = usernames.get(1);
        LunchPicker lunchPicker = createDefaultLunchPicker();

        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user1);
        lunchPicker = lunchPickerService.submitLunchOption(submitRequest(lunchPicker, "ShopA"), user0);

        PickLunchOptionRequest pickReq = pickRequest(lunchPicker);

        assertThrows(PickLunchException.class, () -> lunchPickerService.pickLunchOption(pickReq, user0));
        assertNotEquals(LunchPicker.State.PICKED, lunchPicker.getState());

        lunchPicker = lunchPickerService.pickLunchOption(pickReq, user1);
        assertEquals(LunchPicker.State.PICKED, lunchPicker.getState());
        assertNotNull(lunchPicker.getPickedLunchOption());
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