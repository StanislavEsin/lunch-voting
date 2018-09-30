package kz.stanislav.voting.service;

import kz.stanislav.voting.persistence.model.Vote;
import kz.stanislav.voting.persistence.dao.VoteRepository;
import kz.stanislav.voting.persistence.dao.UserRepository;
import kz.stanislav.voting.persistence.dao.CrudRestaurantRepository;
import kz.stanislav.voting.util.WorkingEnvironment;
import kz.stanislav.voting.util.exception.VoteRepeatException;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final CrudRestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final WorkingEnvironment workingEnvironment;

    @Autowired
    public VoteServiceImpl(VoteRepository voteRepository, CrudRestaurantRepository restaurantRepository,
                           UserRepository userRepository, WorkingEnvironment workingEnvironment) {
        this.voteRepository = voteRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.workingEnvironment = workingEnvironment;
    }

    @Override
    public Integer getVotesCount(Integer restaurantId) {
        return voteRepository.countAllByRestaurantId(restaurantId);
    }

    @Override
    public Integer getVotesCountByDate(Integer restaurantId, LocalDate date) {
        return voteRepository.countAllByRestaurantIdAndDate(restaurantId, date);
    }

    @Override
    public Integer getVotesCountForPeriod(Integer restaurantId, LocalDate startDate, LocalDate endDate) {
        return voteRepository.countAllByRestaurantIdAndDateBetween(restaurantId, startDate, endDate);
    }

    @Override
    public Boolean isVoted(Integer restaurantId, Integer userId) {
        return voteRepository.
                countByRestaurantIdAndUserIdAndDate(restaurantId, userId, workingEnvironment.getWorkingDate()) > 0;
    }

    @Override
    @Transactional
    public Vote vote(Integer restaurantId, Integer userId) {
        Optional<Vote> vote = voteRepository.findByUserIdAndDate(userId, workingEnvironment.getWorkingDate());

        return voteRepository.save(vote.map(v -> {
            if (workingEnvironment.isNotPossibleRepeatVoting()) {
                throw new VoteRepeatException("today the voting time has expired");
            }
            v.setRestaurant(restaurantRepository.getOne(restaurantId));
            return v;
        }).orElse(new Vote(workingEnvironment.getWorkingDate(), userRepository.getOne(userId),
                restaurantRepository.getOne(restaurantId))));
    }
}