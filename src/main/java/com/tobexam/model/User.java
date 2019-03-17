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
	@NonNull private Level level;
	@NonNull private int login;
	@NonNull private int recommend;

    public User() { }

	public User(String id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public User(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
}