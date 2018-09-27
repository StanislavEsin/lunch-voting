package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Menu;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import java.util.Collections;
import java.util.Arrays;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MenuRestController.REST_URL.replace("{restaurantId}", "%s") + "/";
    private static final String REST_URL_ALL_RESTAURANTS = MenuRestController.REST_URL_ALL_RESTAURANTS + "/";
    private static final String[] IGNORING_FIELDS = {"dishes", "restaurant"};

    @Test
    void testGetByAllRestaurantsAndByDate() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-date")
                .param("date", MENU5.getDate().toString())
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(Arrays.asList(MENU1, MENU3, MENU5))));
    }

    @Test
    void testGetByAllRestaurantsAndByDateForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-date")
                .param("date", MENU5.getDate().toString())
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetByAllRestaurantsAndByDateInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-date")
                .param("date", "2017-02-31")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }

    @Test
    void testGetByAllRestaurantsAndByPeriod() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-period")
                .param("startDate", "2017-04-20")
                .param("endDate", "2018-09-14")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(ALL_MENU)));
    }

    @Test
    void testGetByAllRestaurantsAndByPeriodForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-period")
                .param("startDate", "2017-04-20")
                .param("endDate", "2018-09-14")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetByAllRestaurantsAndByPeriodInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL_ALL_RESTAURANTS + "by-period")
                .param("startDate", "2d017-04-20")
                .param("endDate", "2018-09-14")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }

    @Test
    void testGetAllByRestaurant() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(Arrays.asList(MENU1, MENU2))));
    }

    @Test
    void testGetAllByRestaurantForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID))
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGet() throws Exception {
        MockHttpServletRequestBuilder reqBuilder =
                get(format(REST_URL, RESTAURANT1_ID + 1) + (MENU1_ID + 3))
                        .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(MENU4)));
    }

    @Test
    void testGetForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder =
                get(format(REST_URL, RESTAURANT1_ID + 1) + (MENU1_ID + 3))
                        .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder =
                get(format(REST_URL, RESTAURANT1_ID + 1) + (MENU1_ID))
                        .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetByRestaurantAndByDate() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-date")
                .param("date", MENU6.getDate().toString())
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(Collections.singletonList(MENU6))));
    }

    @Test
    void testGetByRestaurantAndByDateForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-date")
                .param("date", MENU6.getDate().toString())
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetByRestaurantAndByDateInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-date")
                .param("date", "2018-09-32")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }

    @Test
    void testGetByRestaurantAndByPeriod() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-period")
                .param("startDate", "2017-04-20")
                .param("endDate", "2018-09-14")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(Arrays.asList(MENU5, MENU6))));
    }

    @Test
    void testGetByRestaurantAndByPeriodForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-period")
                .param("startDate", "2017-04-20")
                .param("endDate", "2018-09-14")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetByRestaurantAndByPeriodInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(format(REST_URL, RESTAURANT1_ID + 2) + "by-period")
                .param("startDate", "2017-04-20")
                .param("endDate", "201s8-09-1t4")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }

    @Test
    void testCreate() throws Exception {
        Menu expected = new Menu(LocalDate.parse("1950-01-01"), RESTAURANT1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
        Menu returned = TestUtil.readFromJson(jsonUtil, action, Menu.class);
        expected.setId(returned.getId());

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, menuRepository.findById(returned.getId()).orElse(null), expected);
    }

    @Test
    void testCreateForbidden() throws Exception {
        Menu expected = new Menu(LocalDate.parse("1950-01-01"), RESTAURANT1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateInvalid() throws Exception {
        Menu expected = new Menu(null, RESTAURANT1);
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testCreateDuplicate() throws Exception {
        Menu expected = new Menu(MENU1.getDate(), MENU1.getRestaurant());
        MockHttpServletRequestBuilder reqBuilder = post(format(REST_URL, RESTAURANT1_ID))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testUpdate() throws Exception {
        Menu expected = new Menu(MENU1_ID, LocalDate.parse("1960-01-01"), MENU1.getRestaurant());
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID) + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        Menu returned = TestUtil.readFromJson(jsonUtil, action, Menu.class);

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, menuRepository.findById(MENU1_ID).orElse(null), expected);
    }

    @Test
    void testUpdateForbidden() throws Exception {
        Menu expected = new Menu(LocalDate.parse("1960-01-01"));
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID) + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        Menu expected = new Menu(LocalDate.parse("1960-01-01"));
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID) + (MENU1_ID + 100))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateRestaurantNotFound() throws Exception {
        Menu expected = new Menu(LocalDate.parse("1960-01-01"));
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID + 100) + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Menu expected = new Menu();
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID) + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdateDuplicate() throws Exception {
        Menu expected = new Menu(LocalDate.parse("2018-08-14"));
        MockHttpServletRequestBuilder reqBuilder = put(format(REST_URL, RESTAURANT1_ID) + MENU1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeIgnoreProps(expected, "restaurant"));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testDelete() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID + 2) + (MENU1_ID + 5))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatchIgnoringFields(IGNORING_FIELDS, menuRepository.findAll(), Arrays.asList(MENU1, MENU2, MENU3,
                MENU4, MENU5));
    }

    @Test
    void testDeleteForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID + 2) + (MENU1_ID + 5))
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(format(REST_URL, RESTAURANT1_ID + 2) + (MENU1_ID + 2))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}