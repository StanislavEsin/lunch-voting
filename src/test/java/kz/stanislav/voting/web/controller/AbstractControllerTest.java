package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.dao.*;
import kz.stanislav.voting.LunchVotingApplication;
import kz.stanislav.voting.web.json.JsonUtil;
import kz.stanislav.voting.util.exception.ErrorType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    protected JsonUtil jsonUtil;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CrudRestaurantRepository restaurantRepository;

    @Autowired
    protected MenuRepository menuRepository;

    @Autowired
    protected DishRepository dishRepository;

    @Autowired
    protected VoteRepository voteRepository;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }
}