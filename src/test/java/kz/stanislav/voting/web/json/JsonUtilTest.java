package kz.stanislav.voting.web.json;

import kz.stanislav.voting.persistence.model.Restaurant;
import kz.stanislav.voting.LunchVotingApplication;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static kz.stanislav.voting.utilsfortesting.TestData.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
class JsonUtilTest {
    @Autowired
    JsonUtil jsonUtil;

    @Test
    void testWriteCollectionIgnoreProps() throws Exception {
        String expected = "[{\"id\":100000},{\"id\":100001},{\"id\":100002}]";

        String result = jsonUtil.writeIgnoreProps(Arrays.asList(MENU1, MENU2, MENU3), "date", "restaurant", "dishes");

        assertMatch(result, expected);
    }

    @Test
    void testWriteIgnoreProps() throws Exception {
        String expected = "{\"id\":100000}";

        String result = jsonUtil.writeIgnoreProps(MENU1, "date", "restaurant", "dishes");

        assertMatch(result, expected);
    }

    @Test
    void testWriteValue() throws Exception {
        String expected = "{\"id\":100000,\"name\":\"Manana\"}";

        String result = jsonUtil.writeValue(RESTAURANT1);

        assertMatch(result, expected);
    }

    @Test
    void testWriteAdditionOneProps() throws Exception {
        String expected = "{\"id\":100000,\"name\":\"Manana\",\"FieldTest\":\"ValueTest\"}";

        String result = jsonUtil.writeAdditionProps(RESTAURANT1, "FieldTest", "ValueTest");

        assertMatch(result, expected);
    }

    @Test
    void testWriteAdditionSomeProps() throws Exception {
        Map<String, Object> someProps = new HashMap<>();
        someProps.put("FieldTest1", "ValueTest1");
        someProps.put("FieldTest2", "ValueTest2");
        someProps.put("FieldTest3", "ValueTest3");

        String expected = "{\"id\":100000,\"name\":\"Manana\",\"FieldTest2\":\"ValueTest2\"," +
                "\"FieldTest3\":\"ValueTest3\",\"FieldTest1\":\"ValueTest1\"}";

        String result = jsonUtil.writeAdditionProps(RESTAURANT1, someProps);

        assertMatch(result, expected);
    }

    @Test
    void testReadValue() throws Exception {
        Restaurant result = jsonUtil.readValue("{\"id\":100000,\"name\":\"Manana\"}", Restaurant.class);

        assertMatch(result, RESTAURANT1);
    }
}