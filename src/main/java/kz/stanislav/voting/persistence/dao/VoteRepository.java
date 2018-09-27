package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.persistence.model.Vote;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Integer countAllByRestaurantId(Integer restaurant_id);

    Integer countAllByRestaurantIdAndDate(Integer restaurant_id, LocalDate date);

    Integer countAllByRestaurantIdAndDateBetween(Integer restaurant_id, LocalDate startDate, LocalDate endDate);

    Integer countByRestaurantIdAndUserIdAndDate(Integer restaurant_id, Integer user_id, LocalDate date);

    Optional<Vote> findByUserIdAndDate(Integer user_id, LocalDate date);
}