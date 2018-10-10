package eci.analite.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eci.analite.data.UserRepository;
import eci.analite.data.model.User;
import eci.analite.data.service.UserService;

@Service
public class UserServiceMongo implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserByUsername(String username) throws UserServiceException {
		if (userRepository.existsByUsername(username)) {
			return userRepository.findByUsername(username);
		} else {
			throw new UserServiceException("The user doesn't exist");
		}
	}

	@Override
	public User addUSer(User user) throws UserServiceException {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new UserServiceException(user, "User already exists");
		}
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user) {
		// TODO Auto-generated method stub
		return null;

	}
}
