package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.CustomUserDetails;
import org.tech.finalprojectadb.entity.User;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.UsernameAlreadyExistsException;
import org.tech.finalprojectadb.repository.UserRepository;
import org.tech.finalprojectadb.util.Action;
import org.tech.finalprojectadb.util.RegistrationForm;
import org.tech.finalprojectadb.util.UserFullInfo;

import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final UserActionService userActionService;

	private final PasswordEncoder passwordEncoder;

	public String registerNewUser(RegistrationForm form) {
		if (isUsernameExists(form.username())) {
			throw new UsernameAlreadyExistsException("User with username: { " + form.username() + " } already exists");
		}

		if (! isValidPassword(form)) {
			throw new IllegalArgumentException("Passwords does not match");
		}

		User user = new User(form.username(), passwordEncoder.encode(form.password()));
		userRepository.insert(user);
		return "User '" + form.username() + "' successfully registered";
	}

	public User updateUsername(String id, String newUserName) {
		if (id == null || newUserName == null) {
			log.error("Cannot update username: id: {}, newUserName: {}", id, newUserName);
			throw new IllegalArgumentException("Cannot update username: id: " + id + ", newUserName: " + newUserName);
		} else if (isUsernameExists(newUserName)) {
			log.error("User with username: '{}' already exists", newUserName);
			throw new UsernameAlreadyExistsException("User with username: { " + newUserName + " } already exists");
		}

		log.info("Updating user username. new username: {}", newUserName);
		return updateUser(id, (us) -> us.setUsername(newUserName));
	}

	public User findById(String userId) {
		return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User: " + userId + " is not found"));
	}

	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User: " + username + " is not found"));
	}

	public boolean isUsernameExists(String username) {
		return userRepository.existsUserByUsername(username);
	}

	private User updateUser(String id, Consumer<User> consumer) {
		if (id == null) {
			log.error("User is null, nothing to find");
			throw new EntityNotFoundException("User is null, nothing to find");
		}

		User user = findById(id);
		consumer.accept(user);
		userRepository.save(user);
		return user;
	}

	private boolean isValidPassword(RegistrationForm form) {
		return form.password().equals(form.passwordConfirmation());
	}

	public String getCurrentUserId() {
		CustomUserDetails authentication = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return authentication.getUserId();
	}

	public UserFullInfo getFullInfo(String userId, Integer limit) {
		String username = findById(userId).getUsername();

		int lim = 10; //default value, if limit == null
		if (limit != null) {
			lim = limit;
		}

		List<UserAction> lastActivity = userActionService.filterByUserIdAndLimit(userId, lim);
		return new UserFullInfo(username, lastActivity);
	}

	public List<UserAction> getUserHistory(String userId, Integer limit, Boolean all) {
		List<UserAction> userActionList = getFullInfo(userId, limit).userActivity();

		if (all != null && !all) {
			return userActionList.stream().filter(userAction -> userAction.getAction() != Action.VIEW).toList();
		}

		return userActionList;
	}
}
