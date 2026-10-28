package com.gcu.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gcu.business.OrderService;
import com.gcu.business.UserServiceInterface;
import com.gcu.data.entity.User;
import com.gcu.model.LoginForm;
import com.gcu.model.SignUpForm;

// for now the logged in user is just saved in the session
// this will get replaced when we learn Spring Security
@Controller
public class AccountController {

	private final UserServiceInterface userService;
	private final OrderService orderService;

	public AccountController(UserServiceInterface userService, OrderService orderService) {
		this.userService = userService;
		this.orderService = orderService;
	}

	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		model.addAttribute("title", "Sign Up");
		model.addAttribute("signUpForm", new SignUpForm());
		return "sign-up";
	}

	@PostMapping("/sign-up")
	public String signUp(@Valid @ModelAttribute SignUpForm signUpForm, BindingResult result, Model model,
			HttpSession session) {
		if (signUpForm.getEmail() != null && userService.emailTaken(signUpForm.getEmail())) {
			result.rejectValue("email", "duplicate", "An account with that email already exists");
		}
		if (result.hasErrors()) {
			model.addAttribute("title", "Sign Up");
			return "sign-up";
		}
		User user = userService.register(signUpForm);
		saveToSession(session, user);
		return "redirect:/account";
	}

	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("title", "Login");
		model.addAttribute("loginForm", new LoginForm());
		return "login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, Model model,
			HttpSession session) {
		User user = null;
		if (!result.hasErrors()) {
			user = userService.login(loginForm.getEmail(), loginForm.getPassword());
			if (user == null) {
				result.reject("badLogin", "Email or password is incorrect");
			}
		}
		if (result.hasErrors()) {
			model.addAttribute("title", "Login");
			return "login";
		}
		saveToSession(session, user);
		return "redirect:/account";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}

	@GetMapping("/account")
	public String account(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		User user = userService.getUser(userId);
		if (user == null) {
			// user is gone (for example the in-memory list was reset by a restart)
			session.invalidate();
			return "redirect:/login";
		}
		model.addAttribute("title", "My Account");
		model.addAttribute("user", user);
		model.addAttribute("orders", orderService.getOrdersForUser(userId));
		return "account";
	}

	private void saveToSession(HttpSession session, User user) {
		session.setAttribute("userId", user.getId());
		session.setAttribute("userName", user.getFirstName());
	}
}
