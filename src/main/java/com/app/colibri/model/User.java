package com.app.colibri.model;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class User implements Serializable {
	private static final long serialVersionUID = -4476017379429717808L;

	public static final String GUEST = "guest";

	private String userName;
	private String userPasswordHash;
	private boolean autoEnter = false;

	public User() {
	}

	public User(String userName, String userPasswordHash) {
		this.userName = userName;
		this.userPasswordHash = userPasswordHash;
	}

}
