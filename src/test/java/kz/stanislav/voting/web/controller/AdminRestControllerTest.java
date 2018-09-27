package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.User;
import kz.stanislav.voting.persistence.model.Role;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import java.util.Collections;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class AdminRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestController.REST_URL + "/";
    private static final String[] IGNORING_FIELDS = {"password"};

    @Test
    void testGetAll() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL).with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(jsonUtil.writeValue(
                        Arrays.asList(ADMIN, USER_ONE, USER_TWO)))
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
    void testGet() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + ADMIN_ID)
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonUtil.writeValue(ADMIN)));
    }

    @Test
    void testGetForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + ADMIN_ID)
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + (ADMIN_ID + 100))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetByEmail() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "by-email")
                .param("email", ADMIN.getEmail())
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonUtil.writeValue(ADMIN)));
    }

    @Test
    void testGetByEmailForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "by-email")
                .param("email", ADMIN.getEmail())
                .with(TestUtil.userHttpBasic(USER_TWO));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetByEmailNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = get(REST_URL + "by-email")
                .param("email", "fortest@test.com")
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testCreate() throws Exception {
        User expected = new User("New", "new@gmail.com", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, expected, expected.getPassword()));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
        User returned = TestUtil.readFromJson(jsonUtil, action, User.class);
        expected.setId(returned.getId());

        User result = userRepository.findById(returned.getId()).orElse(null);

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, result, expected);
        assertMatch(passwordEncoder.matches(expected.getPassword(), result.getPassword()), true);
    }

    @Test
    void testCreateWithoutRoles() throws Exception {
        User expected = new User("New", "new@gmail.com", "newPass");
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, expected, expected.getPassword()));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
        User returned = TestUtil.readFromJson(jsonUtil, action, User.class);
        expected.setId(returned.getId());
        expected.setRoles(Collections.singletonList(Role.ROLE_USER));

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, expected);
        assertMatchIgnoringFields(IGNORING_FIELDS, userRepository.findById(returned.getId()).orElse(null), expected);
    }

    @Test
    void testCreateForbidden() throws Exception {
        User expected = new User("New", "new@gmail.com", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_TWO))
                .content(jsonWithPassword(jsonUtil, expected, expected.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateInvalid() throws Exception {
        User expected = new User(null, "", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, expected, expected.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testCreateDuplicate() throws Exception {
        User expected = new User(null, "New", USER_TWO.getEmail(), "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        MockHttpServletRequestBuilder reqBuilder = post(REST_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, expected, expected.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testUpdate() throws Exception {
        User updated = new User(USER_ONE);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (ADMIN_ID + 1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, updated, updated.getPassword()));

        ResultActions action = mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isOk());
        User returned = TestUtil.readFromJson(jsonUtil, action, User.class);

        User result = userRepository.findById(ADMIN_ID + 1).orElseGet(null);

        assertMatchIgnoringFields(IGNORING_FIELDS, returned, updated);
        assertMatchIgnoringFields(IGNORING_FIELDS, result, updated);
        assertMatch(passwordEncoder.matches(updated.getPassword(), result.getPassword()), true);
    }

    @Test
    void testUpdateForbidden() throws Exception {
        User updated = new User(USER_ONE);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (ADMIN_ID + 1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(USER_TWO))
                .content(jsonWithPassword(jsonUtil, updated, updated.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        User updated = new User(USER_ONE);
        updated.setId(ADMIN_ID + 100);
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (ADMIN_ID + 100))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, updated, updated.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        User updated = new User(USER_ONE);
        updated.setName("");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (ADMIN_ID + 1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, updated, updated.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdateDuplicate() throws Exception {
        User updated = new User(USER_ONE);
        updated.setName("UpdatedName");
        updated.setEmail(USER_TWO.getEmail());
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        MockHttpServletRequestBuilder reqBuilder = put(REST_URL + (ADMIN_ID + 1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(TestUtil.userHttpBasic(ADMIN))
                .content(jsonWithPassword(jsonUtil, updated, updated.getPassword()));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void testDelete() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (ADMIN_ID + 2))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatchIgnoringFields(IGNORING_FIELDS, userRepository.findAll(), ADMIN, USER_ONE);
    }

    @Test
    void testDeleteForbidden() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (ADMIN_ID + 2))
                .with(TestUtil.userHttpBasic(USER_ONE));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        MockHttpServletRequestBuilder reqBuilder = delete(REST_URL + (ADMIN_ID + 100))
                .with(TestUtil.userHttpBasic(ADMIN));

        mockMvc.perform(reqBuilder)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}