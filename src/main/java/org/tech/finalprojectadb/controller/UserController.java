package org.tech.finalprojectadb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.service.UserService;
import org.tech.finalprojectadb.util.RegistrationForm;
import org.tech.finalprojectadb.util.UserFullInfo;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/registration")
	public ResponseEntity<String> registerUser(@RequestBody RegistrationForm body) {
		try {
			return new ResponseEntity<>(userService.registerNewUser(body), HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/me")
	public ResponseEntity<UserFullInfo> getUserInformation(@RequestParam(required = false) Integer limit) {
		String currentUserId = userService.getCurrentUserId();
		return new ResponseEntity<>(userService.getFullInfo(currentUserId, limit), HttpStatus.OK);
	}

	@GetMapping("/me/username")
	public ResponseEntity<String> getUsername() {
		String currentUserId = userService.getCurrentUserId();
		return new ResponseEntity<>(userService.findById(currentUserId).getUsername(), HttpStatus.OK);
	}

	@GetMapping("/me/history")
	public ResponseEntity<List<UserAction>> getUserHistory(@RequestParam(required = false) Integer limit,
														   @RequestParam(required = false) Boolean all) {
		String currentUserId = userService.getCurrentUserId();
		List<UserAction> userActionList = userService.getUserHistory(currentUserId, limit, all);
		return new ResponseEntity<>(userActionList, HttpStatus.OK);
	}

}
