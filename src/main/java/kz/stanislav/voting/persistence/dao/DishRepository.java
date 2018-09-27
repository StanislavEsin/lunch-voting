package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.persistence.model.Dish;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM " +
            "Menu m INNER JOIN Dish d on m.id = d.menu.id " +
            "WHERE d.id=:id AND m.restaurant.id=:restaurantId AND m.id=:menuId")
    boolean existsByRestaurantIdAndMenuId(@Param("id") int id,
                                          @Param("restaurantId") int restaurantId,
                                          @Param("menuId") int menuId);
}