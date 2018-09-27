package kz.stanislav.voting.utilsfortesting;

import kz.stanislav.voting.persistence.model.User;
import kz.stanislav.voting.util.WorkingEnvironment;
import kz.stanislav.voting.web.json.JsonUtil;
import java.time.LocalDate;
import java.io.UnsupportedEncodingException;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

public class TestUtil {
    public static String getContent(ResultActions action) throws UnsupportedEncodingException {
        return action.andReturn().getResponse().getContentAsString();
    }

    public static RequestPostProcessor userHttpBasic(User user) {
        return SecurityMockMvcRequestPostProcessors.httpBasic(user.getEmail(), user.getPassword());
    }

    public static <T> T readFromJson(JsonUtil jsonUtil, ResultActions action, Class<T> clazz) throws UnsupportedEncodingException {
        return jsonUtil.readValue(getContent(action), clazz);
    }

    public static void initMockWorkingEnvironment(WorkingEnvironment workingEnvironment, LocalDate workingDate,
                                            boolean isNotPossibleRepeatVoting) {
        Mockito.doReturn(workingDate)
                .when(workingEnvironment)
                .getWorkingDate();

        Mockito.doReturn(isNotPossibleRepeatVoting)
                .when(workingEnvironment)
                .isNotPossibleRepeatVoting();
    }
}