package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.persistence.model.Dish;
import kz.stanislav.voting.web.dto.RestaurantDto;
import kz.stanislav.voting.util.WorkingEnvironment;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.ResultSetExtractor;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final WorkingEnvironment workingEnvironment;

    @Autowired
    public RestaurantRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                    WorkingEnvironment workingEnvironment) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.workingEnvironment = workingEnvironment;
    }

    @Override
    public List<RestaurantDto> getRestaurantDtoWithDishAndVote(int userId) {
        String sql = "SELECT r.id, r.name, md.dish_id, md.dish_name, md.price, vc.vote" +
                " FROM public.restaurants AS r" +
                " LEFT JOIN (SELECT m.id AS menu_id, m.restaurant_id, d.id AS dish_id, d.name AS dish_name, d.price" +
                " FROM public.menu AS m JOIN public.dishes AS d ON m.id = d.menu_id WHERE m.date=:date)" +
                " AS md ON md.restaurant_id = r.id" +
                " LEFT JOIN (SELECT count(*) as vote, v.restaurant_id" +
                " FROM public.vote AS v WHERE v.date=:date AND v.user_id=:user_id GROUP BY v.restaurant_id)" +
                " AS vc ON r.id = vc.restaurant_id;";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("date", workingEnvironment.getWorkingDate())
                .addValue("user_id", userId);

        List<RestaurantDto> result = namedParameterJdbcTemplate.query(sql, parameters, (ResultSetExtractor<List<RestaurantDto>>) rs -> {
            Map<Integer, RestaurantDto> map = new HashMap<>();
            RestaurantDto restaurantDto;

            while (rs.next()) {
                int id = rs.getInt("id");
                restaurantDto = map.get(id);

                if (restaurantDto == null) {
                    int vote = rs.getInt("vote");
                    restaurantDto = new RestaurantDto(id, rs.getString("name"), new ArrayList<>(), vote > 0);
                    map.put(id, restaurantDto);
                }

                int dish_id = rs.getInt("dish_id");
                if (dish_id > 0) {
                    Dish dish = new Dish(dish_id, rs.getString("dish_name"), rs.getInt("price"), null);
                    restaurantDto.getDishes().add(dish);
                }
            }

            return new ArrayList<>(map.values());
        });

        if (result != null) {
            result.sort(Comparator.comparing(RestaurantDto::getName));
        }

        return result;
    }
}