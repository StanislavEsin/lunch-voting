package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.web.dto.RestaurantDto;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.LunchVotingApplication;
import kz.stanislav.voting.util.WorkingEnvironment;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static kz.stanislav.voting.utilsfortesting.TestData.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
class RestaurantRepositoryImplTest {
    @Autowired
    RestaurantRepository restaurantRepository;

    @MockBean
    private WorkingEnvironment workingEnvironment;

    @Test
    void testGetRestaurantDtoWithDishAndVote() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, MENU2.getDate(), false);

        List<RestaurantDto> result = restaurantRepository.
                getRestaurantDtoWithDishAndVote(workingEnvironment, USER_ONE.getId());


        assertMatch(result, Arrays.asList(RESTAURANTDTO1, RESTAURANTDTO2, RESTAURANTDTO3));
    }
}