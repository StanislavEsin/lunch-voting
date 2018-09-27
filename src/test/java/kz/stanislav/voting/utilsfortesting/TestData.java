package kz.stanislav.voting.utilsfortesting;

import kz.stanislav.voting.persistence.model.*;
import kz.stanislav.voting.web.dto.RestaurantDto;
import kz.stanislav.voting.web.json.JsonUtil;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

public class TestData {
    public static final int ADMIN_ID = 100000;
    public static final User ADMIN = new User(ADMIN_ID,
            "Admin", "admin@gmail.com", "admin", Role.ROLE_USER, Role.ROLE_ADMIN);
    public static final User USER_ONE = new User(ADMIN_ID + 1,
            "UserOne", "user_one@yandex.ru", "password", Role.ROLE_USER);
    public static final User USER_TWO = new User(ADMIN_ID + 2,
            "UserTwo", "user_two@yandex.ru", "password", Role.ROLE_USER);

    public static final int RESTAURANT1_ID = 100000;
    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, "Manana");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT1_ID + 1, "Arcobaleno");
    public static final Restaurant RESTAURANT3 = new Restaurant(RESTAURANT1_ID + 2, "Cosmo");

    public static final int MENU1_ID = 100000;
    public static final Menu MENU1 = new Menu(MENU1_ID, LocalDate.parse("2017-05-20"), RESTAURANT1);
    public static final Menu MENU2 = new Menu(MENU1_ID + 1, LocalDate.parse("2018-08-14"), RESTAURANT1);
    public static final Menu MENU3 = new Menu(MENU1_ID + 2, LocalDate.parse("2017-05-20"), RESTAURANT2);
    public static final Menu MENU4 = new Menu(MENU1_ID + 3, LocalDate.parse("2018-08-14"), RESTAURANT2);
    public static final Menu MENU5 = new Menu(MENU1_ID + 4, LocalDate.parse("2017-05-20"), RESTAURANT3);
    public static final Menu MENU6 = new Menu(MENU1_ID + 5, LocalDate.parse("2018-08-14"), RESTAURANT3);
    public static final List<Menu> ALL_MENU = Collections.unmodifiableList(
            Arrays.asList(MENU1, MENU2, MENU3, MENU4, MENU5, MENU6));

    public static final int DISH1_ID = 100000;
    public static final Dish DISH1 = new Dish(DISH1_ID, "Старое пиво", 540, MENU1);
    public static final Dish DISH2 = new Dish(DISH1_ID + 1, "Старый салат", 1200, MENU1);
    public static final Dish DISH3 = new Dish(DISH1_ID + 2, "Пиво", 600, MENU2);
    public static final Dish DISH4 = new Dish(DISH1_ID + 3, "Салат", 1450, MENU2);
    public static final Dish DISH5 = new Dish(DISH1_ID + 4, "Старый хлеб", 200, MENU3);
    public static final Dish DISH6 = new Dish(DISH1_ID + 5, "Старый шашлык", 4500, MENU3);
    public static final Dish DISH7 = new Dish(DISH1_ID + 6, "Хлеб", 350, MENU4);
    public static final Dish DISH8 = new Dish(DISH1_ID + 7, "Шашлык", 5000, MENU4);
    public static final Dish DISH9 = new Dish(DISH1_ID + 8, "Старая семга", 1300, MENU5);
    public static final Dish DISH10 = new Dish(DISH1_ID + 9, "Старый сок", 400, MENU5);
    public static final Dish DISH11 = new Dish(DISH1_ID + 10, "Семга", 1400, MENU6);
    public static final Dish DISH12 = new Dish(DISH1_ID + 11, "Сок", 450, MENU6);

    public static final int VOTE1_ID = 100000;
    public static final Vote VOTE1 = new Vote(VOTE1_ID, LocalDate.of(2017, 5, 20), ADMIN, RESTAURANT2);
    public static final Vote VOTE2 = new Vote(VOTE1_ID + 1, LocalDate.of(2017, 5, 20), USER_ONE, RESTAURANT2);
    public static final Vote VOTE3 = new Vote(VOTE1_ID + 2, LocalDate.of(2017, 5, 20), USER_TWO, RESTAURANT3);
    public static final Vote VOTE4 = new Vote(VOTE1_ID + 3, LocalDate.of(2018, 8, 14), ADMIN, RESTAURANT3);
    public static final Vote VOTE5 = new Vote(VOTE1_ID + 4, LocalDate.of(2018, 8, 14), USER_ONE, RESTAURANT3);
    public static final Vote VOTE6 = new Vote(VOTE1_ID + 5, LocalDate.of(2018, 8, 14), USER_TWO, RESTAURANT3);

    public static final RestaurantDto RESTAURANTDTO1 = new RestaurantDto(
            RESTAURANT2.getId(), RESTAURANT2.getName(), Arrays.asList(DISH7, DISH8), false);

    public static final RestaurantDto RESTAURANTDTO2 = new RestaurantDto(
            RESTAURANT3.getId(), RESTAURANT3.getName(), Arrays.asList(DISH11, DISH12), true);

    public static final RestaurantDto RESTAURANTDTO3 = new RestaurantDto(
            RESTAURANT1.getId(), RESTAURANT1.getName(), Arrays.asList(DISH3, DISH4), false);

    public static final RestaurantDto RESTAURANTDTO1_EMPTY_MENU = new RestaurantDto(
            RESTAURANT2.getId(), RESTAURANT2.getName(), Collections.EMPTY_LIST, false);

    public static final RestaurantDto RESTAURANTDTO2_EMPTY_MENU = new RestaurantDto(
            RESTAURANT3.getId(), RESTAURANT3.getName(), Collections.EMPTY_LIST, false);

    public static final RestaurantDto RESTAURANTDTO3_EMPTY_MENU = new RestaurantDto(
            RESTAURANT1.getId(), RESTAURANT1.getName(), Collections.EMPTY_LIST, false);

    public static <T> void assertMatchIgnoringFields(String[] ignoringFields, T actual, T expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, ignoringFields);
    }

    public static <T> void assertMatchIgnoringFields(String[] ignoringFields, Iterable<T> actual, T... expected) {
        assertMatchIgnoringFields(ignoringFields, actual, Arrays.asList(expected));
    }

    public static <T> void assertMatchIgnoringFields(String[] ignoringFields, Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields(ignoringFields).isEqualTo(expected);
    }

    public static <T> void assertMatch(T actual, T expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static <T> void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static <T> void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static String jsonWithPassword(JsonUtil jsonUtil, User user, String passw) {
        return jsonUtil.writeAdditionProps(user, "password", passw);
    }
}