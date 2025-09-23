package golunch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import golunch.exception.GoLunchException;
import golunch.model.CreateLunchPickerRequest;
import golunch.model.ErrorResponse;
import golunch.model.PickLunchOptionRequest;
import golunch.model.SubmitLunchOptionRequest;
import golunch.model.entity.LunchPicker;
import golunch.service.LunchPickerService;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LunchPickerController.class)
class LunchPickerControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LunchPickerService lunchPickerService;

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/lunch-picker"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testListLunchPickers() throws Exception {
        List<LunchPicker> lunchPickers = List.of(
                LunchPicker.builder().id(1L).build(),
                LunchPicker.builder().id(2L).build()
        );
        when(lunchPickerService.getLunchPickers(any())).thenReturn(new PageImpl<>(lunchPickers));

        MvcResult result = mockMvc.perform(get("/api/lunch-picker"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        PagedLunchPickers pagedLunchPickers = objectMapper.readValue(respBody, PagedLunchPickers.class);

        assertEquals(lunchPickers.size(), pagedLunchPickers.page.totalElements);
        assertEquals(lunchPickers, pagedLunchPickers.content);
    }

    @Test
    @WithMockUser
    void testGetLunchPickers() throws Exception {
        LunchPicker lunchPicker = LunchPicker.builder().id(1L).build();
        when(lunchPickerService.getLunchPickerById(lunchPicker.getId())).thenReturn(lunchPicker);

        MvcResult result = mockMvc.perform(get("/api/lunch-picker/" + lunchPicker.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        LunchPicker lunchPicker1 = objectMapper.readValue(respBody, LunchPicker.class);

        assertEquals(lunchPicker, lunchPicker1);
    }

    @Test
    @WithMockUser("user1")
    void testCreateLunchPicker() throws Exception {
        CreateLunchPickerRequest request = new CreateLunchPickerRequest();
        request.setWaitTime(Duration.ofMinutes(5));

        when(lunchPickerService.createLunchPicker(any())).thenReturn(new LunchPicker());

        MvcResult result = mockMvc.perform(post("/api/lunch-picker")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        LunchPicker lunchPickers = objectMapper.readValue(respBody, LunchPicker.class);

        assertNotNull(lunchPickers);
    }


    @Test
    @WithMockUser("user1")
    void testErrorResponse() throws Exception {
        GoLunchException exception = new GoLunchException("test error");
        when(lunchPickerService.createLunchPicker(any())).thenThrow(exception);

        MvcResult result = mockMvc.perform(post("/api/lunch-picker")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(respBody, ErrorResponse.class);

        assertEquals(exception.getMessage(), errorResponse.getMessage());
    }

    @Test
    @WithMockUser("user1")
    void testSubmitLunchOption() throws Exception {
        SubmitLunchOptionRequest request = new SubmitLunchOptionRequest();
        request.setLunchPickerId(1L);
        request.setShopName("ShopA");

        when(lunchPickerService.submitLunchOption(request, "user1")).thenReturn(new LunchPicker());

        MvcResult result = mockMvc.perform(post("/api/lunch-picker/option")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        LunchPicker lunchPicker1 = objectMapper.readValue(respBody, LunchPicker.class);

        assertNotNull(lunchPicker1);
    }


    @Test
    @WithMockUser("user1")
    void testPickLunchOption() throws Exception {
        PickLunchOptionRequest request = new PickLunchOptionRequest();
        request.setLunchPickerId(1L);

        when(lunchPickerService.pickLunchOption(request, "user1")).thenReturn(new LunchPicker());

        MvcResult result = mockMvc.perform(post("/api/lunch-picker/pick")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String respBody = result.getResponse().getContentAsString();
        LunchPicker lunchPicker1 = objectMapper.readValue(respBody, LunchPicker.class);

        assertNotNull(lunchPicker1);
    }


    @Data
    static class PagedLunchPickers {
        List<LunchPicker> content;
        PageInfo page;

        @Data
        static class PageInfo {
            long size;
            long number;
            long totalElements;
            long totalPages;
        }
    }

}