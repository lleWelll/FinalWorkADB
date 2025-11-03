package org.tech.finalprojectadb.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {

	@Id
	private String id;

	@Indexed(unique = true)
	private String username;

	private	String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
