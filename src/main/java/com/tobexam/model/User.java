package com.tobexam.model;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class User {
    @NonNull private String id;
    @NonNull private String name;
    @NonNull private String password;

    public User() { }

	public User(String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
}