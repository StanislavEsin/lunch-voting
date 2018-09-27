package kz.stanislav.voting.persistence.dao;

import kz.stanislav.voting.persistence.model.Restaurant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional(readOnly = true)
public interface CrudRestaurantRepository extends JpaRepository<Restaurant, Integer> {

}