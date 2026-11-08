package com.gcu.business;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gcu.data.entity.User;
import com.gcu.model.SignUpForm;

// Milestone 2 version of registration and login: no database.
// Users are kept in a map in memory, so they are gone when the app restarts.
// There is one test account ready to log in with: testuser (or test@aitoystore.com) / password123
// Milestone 3: no @Service anymore. SpringConfig creates this class as the "userService" bean (IoC).
public class InMemoryUserService implements UserServiceInterface {

	private final Map<Long, User> users = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Spring calls this right after it creates the bean (initMethod in SpringConfig)
	@Override
	public void init() {
		System.out.println("InMemoryUserService.init(): adding the test account (no database)");
		SignUpForm test = new SignUpForm();
		test.setFirstName("Test");
		test.setLastName("User");
		test.setEmail("test@aitoystore.com");
		test.setPhone("602-555-1234");
		test.setUsername("testuser");
		test.setPassword("password123");
		register(test);
	}

	// Spring calls this when the app shuts down (destroyMethod in SpringConfig)
	@Override
	public void destroy() {
		System.out.println("InMemoryUserService.destroy(): clearing " + users.size() + " users");
		users.clear();
	}

	@Override
	public boolean emailTaken(String email) {
		return findByEmail(email) != null;
	}

	@Override
	public boolean usernameTaken(String username) {
		return findByUsername(username) != null;
	}

	@Override
	public User register(SignUpForm form) {
		User user = new User();
		user.setId(nextId.getAndIncrement());
		user.setFirstName(form.getFirstName().trim());
		user.setLastName(form.getLastName().trim());
		user.setEmail(form.getEmail().trim().toLowerCase());
		user.setPhone(form.getPhone().trim());
		user.setUsername(form.getUsername().trim());
		user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
		user.onCreate(); // sets the created date like the database version does
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User login(String usernameOrEmail, String password) {
		// if it has an @ it's an email, otherwise treat it as a username
		User user = usernameOrEmail.contains("@") ? findByEmail(usernameOrEmail) : findByUsername(usernameOrEmail);
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

	// usernames are not case sensitive, so "TestUser" and "testuser" are the same account
	private User findByUsername(String username) {
		String key = username.trim();
		for (User user : users.values()) {
			if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(key)) {
				return user;
			}
		}
		return null;
	}
}
