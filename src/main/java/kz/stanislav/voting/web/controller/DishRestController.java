package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Dish;
import kz.stanislav.voting.persistence.dao.DishRepository;
import kz.stanislav.voting.persistence.dao.MenuRepository;
import kz.stanislav.voting.mark.View;
import kz.stanislav.voting.util.ValidationUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = DishRestController.REST_URL)
public class DishRestController {
    private static final Logger logger = LoggerFactory.getLogger(DishRestController.class);
    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu/{menuId}/dishes";

    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;

    @Autowired
    public DishRestController(DishRepository dishRepository, MenuRepository menuRepository,
                              ValidationUtil validationUtil) {
        this.dishRepository = dishRepository;
        this.validationUtil = validationUtil;
        this.menuRepository = menuRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> create(@PathVariable("restaurantId") int restaurantId,
                                       @PathVariable("menuId") int menuId,
                                       @Validated(View.Web.class) @RequestBody Dish dish) {
        logger.info("create {} for menu with id={}", dish, menuId);
        validationUtil.checkNew(dish);

        Dish created = menuRepository.findById(menuId).map(m -> {
            if (m.getRestaurant().getId() != restaurantId) {
                return null;
            }
            dish.setMenu(m);
            return dishRepository.save(dish);
        }).orElseThrow(validationUtil.notFoundWithId("id={}", menuId));

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL  + "/{id}")
                .buildAndExpand(restaurantId, menuId, created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> update(@PathVariable("restaurantId") int restaurantId,
                                       @PathVariable("menuId") int menuId,
                                       @PathVariable("id") int id,
                                       @Validated(View.Web.class) @RequestBody Dish dish) {
        logger.info("update {} for menu with id={}", dish, menuId);
        validationUtil.assureIdConsistent(dish, id);
        validationUtil.checkNotFoundWithId(dishRepository.existsByRestaurantIdAndMenuId(id, restaurantId, menuId), id);
        dish.setMenu(menuRepository.getOne(menuId));

        Dish updated = dishRepository.save(dish);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("restaurantId") int restaurantId,
                       @PathVariable("menuId") int menuId,
                       @PathVariable("id") int id) {
        logger.info("delete dish with id={} for menu with id={}", id, menuId);
        validationUtil.checkNotFoundWithId(dishRepository.existsByRestaurantIdAndMenuId(id, restaurantId, menuId), id);
        dishRepository.deleteById(id);
    }
}