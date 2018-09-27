package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.persistence.model.Menu;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional(readOnly = true)
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Menu> findByRestaurantId(int restaurant_id);

    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Menu> findByRestaurantIdAndId(int restaurant_id, int id);

    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Menu> findByDate(LocalDate date);

    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Menu> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Menu> findByRestaurantIdAndDate(int restaurant_id, LocalDate date);

    @EntityGraph(attributePaths = {"restaurant", "dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Menu> findByRestaurantIdAndDateBetween(int restaurant_id, LocalDate date, LocalDate date2);
}