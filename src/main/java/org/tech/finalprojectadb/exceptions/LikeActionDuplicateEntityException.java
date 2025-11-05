package org.tech.finalprojectadb.exceptions;

public class LikeActionDuplicateEntityException extends RuntimeException {
	public LikeActionDuplicateEntityException() {
	}

	public LikeActionDuplicateEntityException(String message) {
		super(message);
	}

	public LikeActionDuplicateEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public LikeActionDuplicateEntityException(Throwable cause) {
		super(cause);
	}

	public LikeActionDuplicateEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
