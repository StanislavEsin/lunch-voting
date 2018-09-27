package kz.stanislav.voting.util.exception;

import org.springframework.lang.NonNull;

public class NotFoundException extends RuntimeException {
    public NotFoundException(@NonNull String msg) {
        super(msg);
    }
}