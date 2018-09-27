package kz.stanislav.voting.util;

import kz.stanislav.voting.mark.HasId;
import kz.stanislav.voting.util.exception.IllegalRequestDataException;
import kz.stanislav.voting.util.exception.NotFoundException;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    public ValidationUtil() {
    }

    public void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean + " must be new (id=null)");
        }
    }

    public void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.getId() != id) {
            throw new IllegalRequestDataException(bean + " must be with id=" + id);
        }
    }

    public void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public void checkNotFound(boolean found, String arg) {
        if (!found) {
            throw new NotFoundException(arg);
        }
    }

    // http://stackoverflow.com/a/28565320/548473
    public Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public Supplier<NotFoundException> notFoundWithId(String msg, Object... args) {
        return () -> new NotFoundException(String.format(msg.replace("{}", "%s"), args));
    }
}