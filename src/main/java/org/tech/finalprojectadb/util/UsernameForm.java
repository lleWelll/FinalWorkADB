package org.tech.finalprojectadb.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Форма для изменения имени пользователя")
public record UsernameForm(String username) {
}
