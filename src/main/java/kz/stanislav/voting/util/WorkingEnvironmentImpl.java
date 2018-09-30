package kz.stanislav.voting.util;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class WorkingEnvironmentImpl implements WorkingEnvironment {
    private final static LocalTime DEFAULT_EXPIRED_TIME_VOTING = LocalTime.parse("11:00");

    @Override
    public LocalDate getWorkingDate() {
        return LocalDate.now();
    }

    @Override
    public boolean isNotPossibleRepeatVoting() {
        return LocalTime.now().isBefore(DEFAULT_EXPIRED_TIME_VOTING);
    }
}