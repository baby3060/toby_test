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
	private String email;

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

	public User(String id, String name, String password, Level level, int login, int recommend, String email) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
		this.email = email;
	}

	public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();

        if( nextLevel == null ) {
            throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
        } else {
			this.level = nextLevel;
		}
	}

}