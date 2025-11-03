package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.User;
import org.tech.finalprojectadb.exceptions.UsernameAlreadyExistsException;
import org.tech.finalprojectadb.repository.UserRepository;
import org.tech.finalprojectadb.util.RegistrationForm;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

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

	public User findByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	public boolean isUsernameExists(String username) {
		return userRepository.existsUserByUsername(username);
	}

	private boolean isValidPassword(RegistrationForm form) {
		return form.password().equals(form.passwordConfirmation());
	}


}
