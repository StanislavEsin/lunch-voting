package kz.stanislav.voting.service;

import kz.stanislav.voting.persistence.model.Vote;
import kz.stanislav.voting.LunchVotingApplication;
import kz.stanislav.voting.utilsfortesting.TestUtil;
import kz.stanislav.voting.util.WorkingEnvironment;
import kz.stanislav.voting.util.exception.VoteRepeatException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import static kz.stanislav.voting.utilsfortesting.TestData.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LunchVotingApplication.class)
@Transactional
class VoteServiceImplTest {
    @Autowired
    VoteService voteService;

    @MockBean
    private WorkingEnvironment workingEnvironment;

    @Test
    void testGetVotesCount() throws Exception {
        int result = voteService.getVotesCount(RESTAURANT2.getId());

        assertMatch(result, 2);
    }

    @Test
    void testGetVotesCountByDate() throws Exception {
        int result = voteService.getVotesCountByDate(RESTAURANT1_ID + 2, VOTE5.getDate());

        assertMatch(result, 3);
    }

    @Test
    void testGetVotesCountForPeriod() throws Exception {
        int result = voteService.getVotesCountForPeriod(RESTAURANT3.getId(), VOTE3.getDate(), VOTE6.getDate());

        assertMatch(result, 4);
    }

    @Test
    void testIsVotedTrue() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE2.getDate(), false);

        boolean result = voteService.
                isVoted(workingEnvironment, RESTAURANT2.getId(), USER_ONE.getId());

        assertMatch(result, true);
    }

    @Test
    void testIsVotedFalse() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, LocalDate.now(), false);

        boolean result = voteService.
                isVoted(workingEnvironment, RESTAURANT2.getId(), USER_ONE.getId());

        assertMatch(result, false);
    }

    @Test
    void testVote() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, LocalDate.now(), false);
        Vote expected = new Vote(LocalDate.now(), USER_ONE, RESTAURANT1);

        Vote result = voteService.vote(workingEnvironment, RESTAURANT1.getId(), USER_ONE.getId());
        expected.setId(result.getId());

        assertMatch(result, expected);
    }

    @Test
    void testVoteRepeatVotingIsPossibleRepeatVoting() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE2.getDate(), false);
        Vote expected = new Vote(VOTE2.getId(), VOTE2.getDate(), VOTE2.getUser(), RESTAURANT1);

        Vote result = voteService.vote(workingEnvironment, RESTAURANT1.getId(), USER_ONE.getId());

        assertMatch(result, expected);
    }

    @Test
    void testVoteRepeatVotingIsNotPossibleRepeatVoting() throws Exception {
        TestUtil.initMockWorkingEnvironment(workingEnvironment, VOTE6.getDate(), true);
        assertThrows(VoteRepeatException.class,
                () -> voteService.vote(workingEnvironment, RESTAURANT1.getId(), USER_TWO.getId()));
    }
}