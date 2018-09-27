package kz.stanislav.voting.util;

import java.time.LocalDate;

public interface WorkingEnvironment {
    LocalDate getWorkingDate();

    boolean isNotPossibleRepeatVoting();
}