package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Restaurant;
import kz.stanislav.voting.persistence.dao.CrudRestaurantRepository;
import kz.stanislav.voting.service.VoteService;
import kz.stanislav.voting.util.ValidationUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.List;
import java.time.LocalDate;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL)
public class RestaurantRestController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantRestController.class);
    static final String REST_URL = "/api/admin/restaurants";

    private final CrudRestaurantRepository crudRestaurantRepository;
    private final VoteService voteService;
    private final ValidationUtil validationUtil;

    @Autowired
    public RestaurantRestController(CrudRestaurantRepository crudRestaurantRepository, VoteService voteService,
                                    ValidationUtil validationUtil) {
        this.crudRestaurantRepository = crudRestaurantRepository;
        this.validationUtil = validationUtil;
        this.voteService = voteService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Restaurant> getAll() {
        logger.info("get all restaurants");
        return crudRestaurantRepository.findAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        logger.info("create {}", restaurant);
        validationUtil.checkNew(restaurant);

        Restaurant created = crudRestaurantRepository.save(restaurant);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> update(@PathVariable("id") int id, @Valid @RequestBody Restaurant restaurant) {
        logger.info("update {}", restaurant);
        validationUtil.assureIdConsistent(restaurant, id);
        validationUtil.checkNotFoundWithId(crudRestaurantRepository.existsById(id), id);

        Restaurant updated = crudRestaurantRepository.save(restaurant);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        logger.info("delete restaurant with id={}", id);
        validationUtil.checkNotFoundWithId(crudRestaurantRepository.existsById(id), id);
        crudRestaurantRepository.deleteById(id);
    }

    @GetMapping(value = "/{id}/votes")
    public Integer getVotesCount(@PathVariable("id") int id){
        return voteService.getVotesCount(id);
    }

    @GetMapping(value = "/{id}/votes/by-date")
    public Integer getVotesCountByDate(@RequestParam(value = "date")
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       @PathVariable("id") int id){
        return voteService.getVotesCountByDate(id, date);
    }

    @GetMapping(value = "/{id}/votes/by-period")
    public Integer getVotesCountByPeriod(@RequestParam(value = "startDate")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam(value = "endDate")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                         @PathVariable("id") int id){
        return voteService.getVotesCountForPeriod(id, startDate, endDate);
    }
}