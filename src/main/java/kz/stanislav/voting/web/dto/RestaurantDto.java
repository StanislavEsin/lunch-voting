package kz.stanislav.voting.web.dto;

import kz.stanislav.voting.persistence.model.Dish;
import java.util.List;

public class RestaurantDto extends BaseDto {
    private final String name;
    private final List<Dish> dishes;
    private final Boolean vote;

    public RestaurantDto(Integer id, String name, List<Dish> dishes, Boolean vote) {
        super(id);
        this.name = name;
        this.dishes = dishes;
        this.vote = vote;
    }

    public String getName() {
        return name;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public Boolean getVote() {
        return vote;
    }
}