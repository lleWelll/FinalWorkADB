package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.CustomUserDetails;
import org.tech.finalprojectadb.entity.User;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.exceptions.UsernameAlreadyExistsException;
import org.tech.finalprojectadb.repository.UserRepository;
import org.tech.finalprojectadb.util.Action;
import org.tech.finalprojectadb.util.RegistrationForm;
import org.tech.finalprojectadb.util.UserFullInfo;

import java.util.List;

@Service
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

	public User findById(String userId) {
		return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User: " + userId + " is not found"));
	}

	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User: " + username + " is not found"));
	}

	public boolean isUsernameExists(String username) {
		return userRepository.existsUserByUsername(username);
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
