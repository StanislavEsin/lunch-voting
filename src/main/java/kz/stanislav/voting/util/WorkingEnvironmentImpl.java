package kz.stanislav.voting.util;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkingEnvironmentImpl implements WorkingEnvironment {
    private final LocalDate workingDate;
    private final static LocalTime DEFAULT_EXPIRED_TIME_VOTING = LocalTime.parse("11:00");

    public WorkingEnvironmentImpl(LocalDate workingDate) {
        this.workingDate = workingDate;
    }

    @Override
    public LocalDate getWorkingDate() {
        return workingDate;
    }

    @Override
    public boolean isNotPossibleRepeatVoting() {
        return LocalTime.now().isBefore(DEFAULT_EXPIRED_TIME_VOTING);
    }
}