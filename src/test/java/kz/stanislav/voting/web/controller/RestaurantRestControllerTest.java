package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Restaurant;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class RestaurantRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = RestaurantRestController.REST_URL + "/";

    @Test
    void testGetAll() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL).with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(
                        Arrays.asList(RESTAURANT1, RESTAURANT2, RESTAURANT3)))
                );
    }

    @Test
    void testGetAllForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL).with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreate() throws Exception {
        Restaurant expected = new Restaurant("New restaurant");
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
        Restaurant returned = TestUtil.readFromJson(jsonUtil, action, Restaurant.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(restaurantRepository.findById(returned.getId()).orElse(null), expected);
    }

    @Test
    void testCreateForbidden() throws Exception {
        Restaurant expected = new Restaurant("New restaurant");
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateInvalid() throws Exception {
        Restaurant expected = new Restaurant("");
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
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
        Restaurant expected = new Restaurant("Arcobaleno");
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testUpdate() throws Exception {
        Restaurant expected = new Restaurant(RESTAURANT1);
        expected.setName("Update restaurant");
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        Restaurant returned = TestUtil.readFromJson(jsonUtil, action, Restaurant.class);

        assertMatch(returned, expected);
        assertMatch(restaurantRepository.findById(RESTAURANT1_ID).orElseGet(null), expected);
    }

    @Test
    void testUpdateForbidden() throws Exception {
        Restaurant expected = new Restaurant(RESTAURANT1);
        expected.setName("Update restaurant");
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_ONE))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        Restaurant expected = new Restaurant(RESTAURANT1);
        expected.setId(RESTAURANT1_ID + 100);
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (RESTAURANT1_ID + 100))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonUtil.writeValue(expected));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Restaurant expected = new Restaurant(RESTAURANT1);
        expected.setName("");
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + RESTAURANT1_ID)
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
        Restaurant expected = new Restaurant(RESTAURANT1);
        expected.setName("Cosmo");
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + RESTAURANT1_ID)
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
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (RESTAURANT1_ID + 2))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(restaurantRepository.findAll(), RESTAURANT1, RESTAURANT2);
    }

    @Test
    void testDeleteForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (RESTAURANT1_ID + 2))
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (RESTAURANT1_ID + 100))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetVotesCount() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 1) + "/votes")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void testGetVotesCountForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 1) + "/votes")
                .with(TestUtil.userHttpBasic(USER_TWO));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetVotesCountWenZero() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 5) + "/votes")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testGetVotesCountByDate() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-date")
                .param("date", "2017-05-20")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testGetVotesCountByDateForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-date")
                .param("date", "2017-05-20")
                .with(TestUtil.userHttpBasic(USER_TWO));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetVotesCountByDateWenZero() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-date")
                .param("date", "2016-05-20")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testGetVotesCountByDateInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-date")
                .param("date", "2017-02-31")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }

    @Test
    void testGetVotesCountByPeriod() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-period")
                .param("startDate", "2017-01-01")
                .param("endDate", "2018-12-31")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    void testGetVotesCountByPeriodForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-period")
                .param("startDate", "2017-01-01")
                .param("endDate", "2018-12-31")
                .with(TestUtil.userHttpBasic(USER_TWO));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetVotesCountByPeriodWenZero() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-period")
                .param("startDate", "2017-01-01")
                .param("endDate", "2017-02-25")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testGetVotesCountByPeriodInvalidParameters() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (RESTAURANT1_ID + 2) + "/votes/by-period")
                .param("startDate", "2017-01-31")
                .param("endDate", "2018-11-31")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(errorType(ErrorType.APP_ERROR));
    }
}