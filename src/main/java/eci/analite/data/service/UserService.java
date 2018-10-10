package eci.analite.data.service;

import eci.analite.data.model.User;
import eci.analite.data.service.impl.UserServiceException;

public interface UserService {
	User getUserByUsername(String username) throws UserServiceException;
	User addUSer(User user) throws UserServiceException;
	User updateUser(User user) throws UserServiceException;
}
