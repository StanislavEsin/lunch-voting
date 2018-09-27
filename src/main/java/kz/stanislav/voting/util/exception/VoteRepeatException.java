package kz.stanislav.voting.util.exception;

import org.springframework.lang.NonNull;

public class VoteRepeatException extends RuntimeException {
    public VoteRepeatException(@NonNull String msg) {
        super(msg);
    }
}