package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Vote;
import kz.stanislav.voting.util.WorkingEnvironment;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import java.util.Collections;
import java.util.Arrays;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = ProfileRestController.REST_URL + "/";

    @MockBean
    private WorkingEnvironment workingEnvironment;

    @Test
    void testGetRestaurantsWithDishesAndVoices() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, MENU2.getDate(), false);
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "restaurants")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(
                        Arrays.asList(RESTAURANTDTO1, RESTAURANTDTO2, RESTAURANTDTO3))
                ));
    }

    @Test
    void testGetRestaurantsWithDishesAndVoicesNoMenu() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, LocalDate.now(), false);
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "restaurants")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(
                        Arrays.asList(RESTAURANTDTO1_EMPTY_MENU, RESTAURANTDTO2_EMPTY_MENU, RESTAURANTDTO3_EMPTY_MENU))
                ));
    }

    @Test
    void testGetByRestaurantAndByDate() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, MENU1.getDate(), false);
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "restaurants/" + RESTAURANT1_ID + "/menu")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeIgnoreProps(
                        Collections.singletonList(MENU1), "date", "restaurant")
                ));
    }

    @Test
    void testCheckCurrentVoteTrue() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE5.getDate(), false);
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "restaurants/" + (RESTAURANT1_ID + 2) + "/vote")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckCurrentVoteFalse() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, LocalDate.now(), false);
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "restaurants/" + (RESTAURANT1_ID + 2) + "/vote")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testVote() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, LocalDate.now(), false);
        Vote expected = new Vote(VOTE1_ID + 6, LocalDate.now(), USER_ONE, RESTAURANT1);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL + "restaurants/" + RESTAURANT1_ID + "/vote")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());

        assertMatch(voteRepository.findByUserIdAndDate(USER_ONE.getId(), LocalDate.now()).orElse(null), expected);
    }

    @Test
    void testVoteRepeatVotingIsPossibleRepeatVoting() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE2.getDate(), false);
        Vote expected = new Vote(VOTE2.getId(), VOTE2.getDate(), USER_ONE, RESTAURANT1);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL + "restaurants/" + RESTAURANT1_ID + "/vote")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());

        assertMatch(voteRepository.findByUserIdAndDate(USER_ONE.getId(), VOTE2.getDate()).orElse(null), expected);
    }

    @Test
    void testVoteRepeatVotingIsNotPossibleRepeatVoting() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE6.getDate(), true);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL + "restaurants/" + RESTAURANT1_ID + "/vote")
                .with(TestUtil.userHttpBasic(USER_TWO));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VOTE_REPEAT_ERROR));

        assertMatch(voteRepository.findByUserIdAndDate(USER_TWO.getId(), VOTE6.getDate()).orElse(null), VOTE6);
    }

    @Test
    void testVoteForNonExistentRestaurant() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL + "restaurants/" + (RESTAURANT1_ID + 100) + "/vote")
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.DATA_NOT_FOUND));
    }
}