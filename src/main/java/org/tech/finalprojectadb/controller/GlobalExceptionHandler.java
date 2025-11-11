package org.tech.finalprojectadb.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.UsernameAlreadyExistsException;
import org.tech.finalprojectadb.util.ExceptionResponseDto;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntityNotFoundException.class)
	public ExceptionResponseDto handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {
		log.error(e.getMessage());
		return new ExceptionResponseDto(HttpStatus.NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ExceptionResponseDto handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e, HttpServletRequest req) {
		log.error(e.getMessage());
		return new ExceptionResponseDto(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ExceptionResponseDto handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest req) {
		log.error(e.getMessage());
		return new ExceptionResponseDto(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
	}

}
