package kz.stanislav.voting.web.controller;

import kz.stanislav.voting.persistence.model.Menu;
import kz.stanislav.voting.web.dto.RestaurantDto;
import kz.stanislav.voting.persistence.dao.MenuRepository;
import kz.stanislav.voting.persistence.dao.CrudRestaurantRepository;
import kz.stanislav.voting.persistence.dao.RestaurantRepository;
import kz.stanislav.voting.service.VoteService;
import kz.stanislav.voting.util.ValidationUtil;
import kz.stanislav.voting.util.WorkingEnvironment;
import kz.stanislav.voting.web.AuthorizedUser;
import java.util.List;
import java.time.LocalDate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping(value = ProfileRestController.REST_URL)
public class ProfileRestController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileRestController.class);
    static final String REST_URL = "/api/profile";

    // https://www.baeldung.com/spring-bean-scopes
    @Resource(name = "workingEnvironment")
    WorkingEnvironment workingEnvironment;

    private final VoteService voteService;
    private final CrudRestaurantRepository crudRestaurantRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final ValidationUtil validationUtil;

    @Autowired
    public ProfileRestController(VoteService voteService, CrudRestaurantRepository crudRestaurantRepository,
                                 RestaurantRepository restaurantRepository, MenuRepository menuRepository,
                                 ValidationUtil validationUtil) {
        this.voteService = voteService;
        this.crudRestaurantRepository = crudRestaurantRepository;
        this.restaurantRepository = restaurantRepository;
        this.validationUtil = validationUtil;
        this.menuRepository = menuRepository;
    }

    @GetMapping(value = "/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantDto> getRestaurantsWithDishesAndVoices(@AuthenticationPrincipal AuthorizedUser authUser) {
        logger.info("get restaurants with dishes and voices on the current date={}", LocalDate.now());
        return restaurantRepository.getRestaurantDtoWithDishAndVote(workingEnvironment, authUser.getId());
    }

    @GetMapping(value = "/restaurants/{id}/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Menu> getMenu(@PathVariable("id") int restaurantId) {
        logger.info("get the menu by current date for restaurant with id={}", restaurantId);
        List<Menu> menu = menuRepository.findByRestaurantIdAndDate(restaurantId, workingEnvironment.getWorkingDate());

        menu.forEach(m -> {
            m.setRestaurant(null);
            m.setDate(null);
        });

        return menu;
    }

    @GetMapping(value = "/restaurants/{id}/vote")
    public Boolean checkCurrentVote(@PathVariable("id") int id, @AuthenticationPrincipal AuthorizedUser authUser){
        logger.info("check current vote for the restaurant={} and the user={}", id, authUser.getId());
        return voteService.isVoted(workingEnvironment, id, authUser.getId());
    }

    @PostMapping(value = "/restaurants/{id}/vote")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity vote(@PathVariable("id") int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser){
        logger.info("vote for the restaurant={} and the user={}", restaurantId, authUser.getId());
        validationUtil.checkNotFoundWithId(crudRestaurantRepository.existsById(restaurantId), restaurantId);
        voteService.vote(workingEnvironment, restaurantId, authUser.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}