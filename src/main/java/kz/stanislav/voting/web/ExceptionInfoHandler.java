package kz.stanislav.voting.web;

import kz.stanislav.voting.util.ValidationUtil;
import kz.stanislav.voting.util.exception.ErrorInfo;
import kz.stanislav.voting.util.exception.ErrorType;
import kz.stanislav.voting.util.exception.NotFoundException;
import kz.stanislav.voting.util.exception.VoteRepeatException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private final ValidationUtil validationUtil;

    @Autowired
    public ExceptionInfoHandler(ValidationUtil validationUtil) {
        this.validationUtil = validationUtil;
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        return logAndGetErrorInfo(req, e, true, ErrorType.VALIDATION_ERROR);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorInfo handleAccessDeniedException(HttpServletRequest req, AccessDeniedException e) {
        return logAndGetErrorInfo(req, e, true, ErrorType.APP_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, ErrorType.DATA_NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleError(HttpServletRequest req, MethodArgumentNotValidException e) {
        return logAndGetErrorInfo(req, e, ErrorType.VALIDATION_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(VoteRepeatException.class)
    @ResponseBody
    public ErrorInfo handleError(HttpServletRequest req, VoteRepeatException e) {
        return logAndGetErrorInfo(req, e, true, ErrorType.VOTE_REPEAT_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, ErrorType.APP_ERROR);
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType) {
        Throwable rootCause = validationUtil.getRootCause(e);

        if (logException) {
            logger.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            logger.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }

        return new ErrorInfo(req.getRequestURL(), errorType, rootCause.getLocalizedMessage());
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, MethodArgumentNotValidException e, ErrorType errorType) {
        List<String> details = new ArrayList<>();
        BindingResult result = e.getBindingResult();
        result.getFieldErrors().forEach(fieldError -> {
            String msg = fieldError.getField()+": "+fieldError.getDefaultMessage();
            details.add(msg);
        });

        return new ErrorInfo(req.getRequestURL(), errorType, details.toArray(new String[0]));
    }
}