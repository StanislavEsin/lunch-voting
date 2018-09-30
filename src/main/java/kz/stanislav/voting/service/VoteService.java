package kz.stanislav.voting.service;

import kz.stanislav.voting.persistence.model.Vote;
import kz.stanislav.voting.util.WorkingEnvironment;
import java.time.LocalDate;

public interface VoteService {
    Integer getVotesCount(Integer restaurantId);

    Integer getVotesCountByDate(Integer restaurantId, LocalDate date);

    Integer getVotesCountForPeriod(Integer restaurantId, LocalDate startDate, LocalDate endDate);

    Boolean isVoted(Integer restaurantId, Integer userId);

    Vote vote(Integer restaurantId, Integer userId);
}