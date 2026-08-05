package pt.ulisboa.tecnico.rnl.dei.dms.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(DEIException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public DEIExceptionDto DEIException(DEIException e) {
		return new DEIExceptionDto(e);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public DEIExceptionDto accessDenied(AccessDeniedException e) {
	    return new DEIExceptionDto(new DEIException(ErrorMessage.NOT_AUTHORIZED));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public DEIExceptionDto unexpectedException(Exception e) {
		logger.error(e.getMessage(), e);
		return new DEIExceptionDto(e);
	}
}
