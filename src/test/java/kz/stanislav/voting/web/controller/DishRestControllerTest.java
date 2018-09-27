package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Dish;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static java.lang.String.format;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DishRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = "/api/admin/restaurants/%s/menu/%s/dishes/";
    private static final String[] IGNORING_FIELDS = {"menu"};

    @Test
    void testCreate() throws Exception {
        Dish expected = new Dish("New dish", 1750, MENU1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID, MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
        Dish returned = TestUtil.readFromJson(jsonUtil, action, Dish.class);
        expected.setId(returned.getId());

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, dishRepository.findById(returned.getId()).orElse(null), expected);
    }

    @Test
    void testCreateForbidden() throws Exception {
        Dish expected = new Dish("New dish", 1750, MENU1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID, MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateInvalid() throws Exception {
        Dish expected = new Dish("", null, MENU1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID, MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testCreateDuplicate() throws Exception {
        Dish expected = new Dish(DISH1.getName(), DISH1.getPrice(), MENU1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID, MENU1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testUpdate() throws Exception {
        Dish expected = new Dish(DISH1);
        expected.setName("Update name");
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        Dish returned = TestUtil.readFromJson(jsonUtil, action, Dish.class);

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, dishRepository.findById(DISH1_ID).orElse(null), expected);
    }

    @Test
    void testUpdateForbidden() throws Exception {
        Dish expected = new Dish(DISH1);
        expected.setName("Update name");
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        Dish expected = new Dish(DISH1);
        expected.setId(DISH1_ID + 100);
        expected.setName("Update name");
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + (DISH1_ID + 100))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Dish expected = new Dish(DISH1);
        expected.setName("");
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdateDuplicate() throws Exception {
        Dish expected = new Dish(DISH3);
        expected.setId(DISH4.getId());
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID, MENU1_ID + 1) + (DISH1_ID + 3))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testDelete() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + (DISH1_ID + 1))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatchIgnoringFields(IGNORING_FIELDS, dishRepository.findAll(), Arrays.asList(DISH1, DISH3, DISH4, DISH5,
                DISH6, DISH7, DISH8, DISH9, DISH10, DISH11, DISH12));
    }

    @Test
    void testDeleteForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + (DISH1_ID + 1))
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID, MENU1_ID) + (DISH1_ID + 2))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}