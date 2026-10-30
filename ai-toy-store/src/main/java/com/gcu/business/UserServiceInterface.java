package com.gcu.business;

import com.gcu.data.entity.User;
import com.gcu.model.SignUpForm;

// the controller only talks to this interface, so we can swap how users are stored
// Milestone 2: InMemoryUserService (no database)
// Milestone 3+: UserService (MySQL) by setting app.users.in-memory=false
public interface UserServiceInterface {

	boolean emailTaken(String email);

	User register(SignUpForm form);

	// returns the user if the email and password match, otherwise null
	User login(String email, String password);

	User getUser(Long id);
}
