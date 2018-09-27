package kz.stanislav.voting.util;

import kz.stanislav.voting.persistence.model.User;
import kz.stanislav.voting.LunchVotingApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static kz.stanislav.voting.utilsfortesting.TestData.USER_ONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
class UserUtilTest {
    @Autowired
    UserUtil userUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testPrepareToSave() throws Exception {
        User result = userUtil.prepareToSave(new User(USER_ONE.getId(), USER_ONE.getName(), " FoRteSt@gMaIl.Com ",
                "test", USER_ONE.isEnabled(), USER_ONE.getRoles()), passwordEncoder);

        Assertions.assertThat(result.getEmail()).isEqualTo("fortest@gmail.com");
        Assertions.assertThat(passwordEncoder.matches("test", result.getPassword())).isEqualTo(true);
    }
}