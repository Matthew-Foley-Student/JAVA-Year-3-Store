package com.gcu.business;

import com.gcu.data.entity.User;
import com.gcu.model.SignUpForm;

// the controller only talks to this interface, so we can swap how users are stored
// Milestone 2: InMemoryUserService (no database)
// later, when we add the database: UserService (MySQL) by setting app.users.in-memory=false
public interface UserServiceInterface {

	boolean emailTaken(String email);

	boolean usernameTaken(String username);

	User register(SignUpForm form);

	// returns the user if the username (or email) and password match, otherwise null
	User login(String usernameOrEmail, String password);

	User getUser(Long id);
}
