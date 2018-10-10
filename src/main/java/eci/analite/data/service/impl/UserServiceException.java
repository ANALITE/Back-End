package eci.analite.data.service.impl;

import eci.analite.data.model.User;

public class UserServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	private String msg;

	public UserServiceException(User user, String msg) {
		if (msg.isEmpty() || msg == null && user != null) {
			this.msg = "An error ocurred associated with user: " + user;
		} else {
			this.msg = msg + user;
		}
	}

	public UserServiceException(String msg) {
		this.msg = msg;
		if (msg.isEmpty() || msg == null) {
			this.msg = "An error ocurred while accessing user repository";
		}
	}

	@Override
	public String getMessage() {
		return msg;
	}
}
