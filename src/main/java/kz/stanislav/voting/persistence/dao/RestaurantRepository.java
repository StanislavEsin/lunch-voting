package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.web.dto.RestaurantDto;
import kz.stanislav.voting.util.WorkingEnvironment;
import java.util.List;

public interface RestaurantRepository {
    List<RestaurantDto> getRestaurantDtoWithDishAndVote(int userId);
}