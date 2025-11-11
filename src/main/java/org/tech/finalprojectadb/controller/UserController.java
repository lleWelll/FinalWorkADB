package org.tech.finalprojectadb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.User;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.service.RecommendationService;
import org.tech.finalprojectadb.service.UserService;
import org.tech.finalprojectadb.util.RegistrationForm;
import org.tech.finalprojectadb.util.UserFullInfo;
import org.tech.finalprojectadb.util.UsernameForm;

import java.util.List;

@Tag(name = "UserController", description = "Операции над пользователями")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	private final RecommendationService recommendationService;

	@Operation(summary = "Регистрация нового пользователя")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Регистрирует нового пользователя"),
			@ApiResponse(responseCode = "400", description = "Указанное имя пользователя уже существует, невозможно создать нового пользователя")
	})
	@PostMapping("/registration")
	public ResponseEntity<String> registerUser(@RequestBody RegistrationForm body) {
		try {
			return new ResponseEntity<>(userService.registerNewUser(body), HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Информация о текущем пользователе")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Возвращает информацию о текущем пользователе: Имя пользователя и список последних действий"),
	})
	@GetMapping("/me")
	public ResponseEntity<UserFullInfo> getUserInformation(
			@Parameter(name = "limit", description = "Ограничение для вывода последних действий пользователя (по умолчанию - 10)") @RequestParam(required = false) Integer limit) {
		String currentUserId = userService.getCurrentUserId();
		return new ResponseEntity<>(userService.getFullInfo(currentUserId, limit), HttpStatus.OK);
	}

	@Operation(summary = "Изменение имени пользователя")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Имя пользователя успешно изменено"),
			@ApiResponse(responseCode = "404", description = "Пользователь не найден"),
			@ApiResponse(responseCode = "400", description = "Указанное имя пользователя уже существует")
	})
	@PostMapping("/me/update/username")
	public ResponseEntity<String> updateUserName(@RequestBody UsernameForm username) {
		String currentUserId = userService.getCurrentUserId();
		return new ResponseEntity<>(userService.updateUsername(currentUserId, username.username()).getUsername(), HttpStatus.OK);
	}

	@Operation(summary = "Имя текущего пользователя")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Возвращает имя текущего пользователя"),
	})
	@GetMapping("/me/username")
	public ResponseEntity<String> getUsername() {
		String currentUserId = userService.getCurrentUserId();
		return new ResponseEntity<>(userService.findById(currentUserId).getUsername(), HttpStatus.OK);
	}

	@Operation(summary = "История текущего пользователя")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Возвращает последние действия над продуктами текущего пользователя"),
	})
	@GetMapping("/me/history")
	public ResponseEntity<List<UserAction>> getUserHistory(
			@Parameter(name = "limit", description = "Ограничение для вывода последних действий пользователя (по умолчанию - 10)") @RequestParam(required = false) Integer limit,
			@Parameter(name = "all", description = "Фильтрация действий. При значении 'false' будут получены последние действия пользователя не включая 'VIEW' действия (по умолчанию - true)") @RequestParam(required = false) Boolean all) {
		String currentUserId = userService.getCurrentUserId();
		List<UserAction> userActionList = userService.getUserHistory(currentUserId, limit, all);
		return new ResponseEntity<>(userActionList, HttpStatus.OK);
	}

	@Operation(summary = "Список рекомендованных продуктов")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Возвращает список рекомендованных продуктов на основе его действий"),
	})
	@GetMapping("/me/recommendation")
	public ResponseEntity<List<Product>> getRecommendations(
			@Parameter(name = "limit", description = "Ограничение для вывода количества продуктов (по умолчанию - 10)")  @RequestParam(required = false) Integer limit) {
		String currentUserId = userService.getCurrentUserId();
		List<Product> recommended = recommendationService.getRecommendedProducts(currentUserId, limit);
		return new ResponseEntity<>(recommended, HttpStatus.OK);
	}

}
