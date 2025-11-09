package org.tech.finalprojectadb.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Форма для регистрации нового пользователя")
public record RegistrationForm(String username, String password, String passwordConfirmation) {}
