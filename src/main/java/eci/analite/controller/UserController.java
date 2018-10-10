package eci.analite.controller;

import java.util.Date;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eci.analite.data.model.User;
import eci.analite.data.service.UserService;
import eci.analite.data.service.impl.UserServiceException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@CrossOrigin(origins = "http://localhost:3000")
	public Token login(@RequestBody User login) throws ServletException, UserServiceException {
		String jwtToken;

		if (login.getUsername() == null || login.getPassword() == null) {
			throw new ServletException("Please fill in username and password");
		}

		String username = login.getUsername();
		String password = login.getPassword();

		User user = userService.getUserByUsername(username);
		if (user == null) {
			throw new ServletException("User username not found.");
		}
		String pwd = user.getPassword();
		if (!password.equals(pwd)) {
			throw new ServletException("Invalid login. Please check your name and password.");
		}
		jwtToken = Jwts.builder().setSubject(username).claim("roles", "user").setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();

		return new Token(jwtToken);
	}

	@RequestMapping(value = "/{user_id}", method = RequestMethod.PUT)
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<String> putUser(@PathVariable(value = "user_id") String username, @RequestBody User user) {
		try {
			userService.updateUser(username, user);
			return new ResponseEntity<>("Username updated", HttpStatus.OK);
		} catch (UserServiceException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<String> postUser(@RequestBody User user) {
		try {
			userService.addUSer(user);
			return new ResponseEntity<>("User created", HttpStatus.OK);
		} catch (UserServiceException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	public class Token {

		String access_token;

		public Token(String access_token) {
			this.access_token = access_token;
		}

		public String getAccessToken() {
			return access_token;
		}

		public void setAccessToken(String access_token) {
			this.access_token = access_token;
		}
	}

}