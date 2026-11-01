package com.gcu.business;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gcu.data.UserRepository;
import com.gcu.data.entity.User;
import com.gcu.model.SignUpForm;

// database version of registration and login (MySQL users table)
// turned on with app.users.in-memory=false in application.properties
// needs the username column from Dominick's ai_toy_store_milestone2_updates.sql script
@Service
@ConditionalOnProperty(name = "app.users.in-memory", havingValue = "false")
public class UserService implements UserServiceInterface {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean emailTaken(String email) {
		return userRepository.existsByEmail(email.trim().toLowerCase());
	}

	@Override
	public boolean usernameTaken(String username) {
		return userRepository.existsByUsernameIgnoreCase(username.trim());
	}

	@Override
	public User register(SignUpForm form) {
		User user = new User();
		user.setFirstName(form.getFirstName().trim());
		user.setLastName(form.getLastName().trim());
		user.setEmail(form.getEmail().trim().toLowerCase());
		user.setPhone(form.getPhone().trim());
		user.setUsername(form.getUsername().trim());
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User login(String usernameOrEmail, String password) {
		String key = usernameOrEmail.trim();
		// if it has an @ it's an email, otherwise treat it as a username
		User user = key.contains("@")
				? userRepository.findByEmail(key.toLowerCase()).orElse(null)
				: userRepository.findByUsernameIgnoreCase(key).orElse(null);
		if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
			return user;
		}
		return null;
	}

	@Override
	public User getUser(Long id) {
		return userRepository.findById(id).orElse(null);
	}
}
