package kz.stanislav.voting.util;

import kz.stanislav.voting.persistence.model.Restaurant;
import kz.stanislav.voting.LunchVotingApplication;
import kz.stanislav.voting.util.exception.IllegalRequestDataException;
import kz.stanislav.voting.util.exception.NotFoundException;
import kz.stanislav.voting.utilsfortesting.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
class ValidationUtilTest {
    @Autowired
    ValidationUtil validationUtil;

    @Test
    void testCheckNew() throws Exception {
        Restaurant expected = new Restaurant(TestData.RESTAURANT1);

        assertThrows(IllegalRequestDataException.class, () -> validationUtil.checkNew(expected));
    }

    @Test
    void testAssureIdConsistent() throws Exception {
        Restaurant expected = new Restaurant("new restaurant");

        validationUtil.assureIdConsistent(expected, TestData.RESTAURANT1_ID);

        TestData.assertMatch(expected.getId(), TestData.RESTAURANT1_ID);
    }

    @Test
    void testAssureIdConsistentIllegalRequestDataException() throws Exception {
        Restaurant expected = new Restaurant(TestData.RESTAURANT1);

        assertThrows(IllegalRequestDataException.class,
                () -> validationUtil.assureIdConsistent(expected, (TestData.RESTAURANT1_ID + 1)));
    }

    @Test
    void testCheckNotFoundWithId() throws Exception {
        validationUtil.checkNotFoundWithId(true, TestData.RESTAURANT1_ID);
    }

    @Test
    void testCheckNotFoundWithIdNotFoundException() throws Exception {
        assertThrows(NotFoundException.class, () -> validationUtil.checkNotFoundWithId(false, TestData.RESTAURANT1_ID));
    }

    @Test
    void testCheckNotFound() throws Exception {
        validationUtil.checkNotFound(true, "example");
    }

    @Test
    void testCheckNotFoundNotFoundException() throws Exception {
        assertThrows(NotFoundException.class, () -> validationUtil.checkNotFound(false, "example"));
    }
}