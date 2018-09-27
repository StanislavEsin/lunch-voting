package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Menu;
import kz.stanislav.voting.persistence.dao.MenuRepository;
import kz.stanislav.voting.persistence.dao.CrudRestaurantRepository;
import kz.stanislav.voting.mark.View;
import kz.stanislav.voting.util.ValidationUtil;
import java.util.List;
import java.time.LocalDate;
import java.net.URI;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class MenuRestController {
    private static final Logger logger = LoggerFactory.getLogger(MenuRestController.class);
    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu";
    static final String REST_URL_ALL_RESTAURANTS = "/api/admin/restaurants/menu";
    private static final String msgNotFoundWithId = "no menu found with id={} for restaurant with id={}";

    private final MenuRepository menuRepository;
    private final CrudRestaurantRepository crudRestaurantRepository;
    private final ValidationUtil validationUtil;

    @Autowired
    public MenuRestController(MenuRepository menuRepository, CrudRestaurantRepository crudRestaurantRepository,
                              ValidationUtil validationUtil) {
        this.menuRepository = menuRepository;
        this.validationUtil = validationUtil;
        this.crudRestaurantRepository = crudRestaurantRepository;
    }

    @GetMapping(value = REST_URL_ALL_RESTAURANTS + "/by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getByAllRestaurantsAndByDate(
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("get the menu by date {}", date);
        return menuRepository.findByDate(date);
    }

    @GetMapping(value = REST_URL_ALL_RESTAURANTS + "/by-period", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getByAllRestaurantsAndByPeriod(
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("get the menu for the period from {} to {}", startDate, endDate);
        return menuRepository.findByDateBetween(startDate, endDate);
    }

    @GetMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getAllByRestaurant(@PathVariable("restaurantId") int restaurantId) {
        logger.info("get the menu for restaurant with id={}", restaurantId);
        return menuRepository.findByRestaurantId(restaurantId);
    }

    @GetMapping(value = REST_URL + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Menu get(@PathVariable("restaurantId") int restaurantId, @PathVariable("id") int id) {
        logger.info("get the menu with id={} for restaurant with id={}", id, restaurantId);
        return menuRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(validationUtil.notFoundWithId(msgNotFoundWithId, id, restaurantId));
    }

    @GetMapping(value = REST_URL + "/by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getByRestaurantAndByDate(
            @PathVariable("restaurantId") int restaurantId,
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("get the menu by date {} for restaurant with id={}", date, restaurantId);
        return menuRepository.findByRestaurantIdAndDate(restaurantId, date);
    }

    @GetMapping(value = REST_URL + "/by-period", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getByRestaurantAndByPeriod(
            @PathVariable("restaurantId") int restaurantId,
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("get the menu for the period from {} to {} for restaurant with id={}",
                startDate, endDate, restaurantId);
        return menuRepository.findByRestaurantIdAndDateBetween(restaurantId, startDate, endDate);
    }

    @PostMapping(value = REST_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> create(@PathVariable("restaurantId") int restaurantId,
                                       @Validated(View.Web.class) @RequestBody Menu menu) {
        menu.setRestaurant(crudRestaurantRepository.findById(restaurantId)
                .orElseThrow(validationUtil.notFoundWithId("id={}",restaurantId)));
        validationUtil.checkNew(menu);

        logger.info("create {} for restaurant with id={}", menu, restaurantId);
        Menu created = menuRepository.save(menu);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = REST_URL + "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> update(@PathVariable("restaurantId") int restaurantId,
                                       @PathVariable("id") int id,
                                       @Validated(View.Web.class) @RequestBody Menu menu) {
        Menu updated = menuRepository.findById(id).map(m -> {
            if (m.getRestaurant().getId() != restaurantId) {
                return null;
            }
            menu.setRestaurant(crudRestaurantRepository.getOne(restaurantId));
            validationUtil.assureIdConsistent(menu, id);

            logger.info("update {} for restaurant with id={}", menu, restaurantId);
            menuRepository.save(menu);

            return m;
        }).orElseThrow(validationUtil.notFoundWithId(msgNotFoundWithId, id, restaurantId));

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(REST_URL + "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("restaurantId") int restaurantId, @PathVariable("id") int id) {
        logger.info("delete menu with id={} for restaurant with id={}", id, restaurantId);
        menuRepository.findById(id).map(m -> {
            if (m.getRestaurant().getId() != restaurantId) {
                return null;
            }

            menuRepository.deleteById(id);

            return m;
        }).orElseThrow(validationUtil.notFoundWithId(msgNotFoundWithId, id, restaurantId));
    }
}