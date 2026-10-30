package com.gcu.business;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gcu.data.entity.User;
import com.gcu.model.SignUpForm;

// Milestone 2 version of registration and login: no database.
// Users are kept in a map in memory, so they are gone when the app restarts.
// There is one test account ready to log in with: test@aitoystore.com / password123
@Service
@ConditionalOnProperty(name = "app.users.in-memory", havingValue = "true", matchIfMissing = true)
public class InMemoryUserService implements UserServiceInterface {

	private final Map<Long, User> users = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public InMemoryUserService() {
		SignUpForm test = new SignUpForm();
		test.setFirstName("Test");
		test.setLastName("User");
		test.setEmail("test@aitoystore.com");
		test.setPassword("password123");
		register(test);
	}

	@Override
	public boolean emailTaken(String email) {
		return findByEmail(email) != null;
	}

	@Override
	public User register(SignUpForm form) {
		User user = new User();
		user.setId(nextId.getAndIncrement());
		user.setFirstName(form.getFirstName().trim());
		user.setLastName(form.getLastName().trim());
		user.setEmail(form.getEmail().trim().toLowerCase());
		user.setPhone(form.getPhone());
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		user.onCreate(); // sets the created date like the database version does
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User login(String email, String password) {
		User user = findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
			return user;
		}
		return null;
	}

	@Override
	public User getUser(Long id) {
		return users.get(id);
	}

	private User findByEmail(String email) {
		String key = email.trim().toLowerCase();
		for (User user : users.values()) {
			if (user.getEmail().equals(key)) {
				return user;
			}
		}
		return null;
	}
}
