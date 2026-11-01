package com.gcu.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// registration fields from the Milestone 2 rubric:
// first name, last name, email, phone number, username and password
public class SignUpForm {

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email")
	private String email;

	// the ^$| part lets a blank value through here so only the "required" message shows
	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^$|^[0-9()+\\-. ]{10,20}$", message = "Enter a valid phone number, like 602-555-1234")
	private String phone;

	@NotBlank(message = "Username is required")
	@Pattern(regexp = "^$|^[A-Za-z0-9_]{3,50}$", message = "Username must be 3-50 letters, numbers or underscores")
	private String username;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^$|^.{8,}$", message = "Password must be at least 8 characters")
	private String password;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
