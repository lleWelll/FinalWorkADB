package org.tech.finalprojectadb.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.tech.finalprojectadb.util.Action;
import org.tech.finalprojectadb.util.Category;

import java.time.LocalDateTime;

@Document(collection = "user_actions")
@Data
@NoArgsConstructor
public class UserAction {

	@Id
	private String id;

	@Field(name = "user_id")
	private String userId;

	@Field(name = "product_id")
	private String productId;

	private Category category;

	private Action action;

	private LocalDateTime timestamp;

	public UserAction(String userId, String productId, Action action) {
		this.userId = userId;
		this.productId = productId;
		this.action = action;
		this.timestamp = LocalDateTime.now();
	}

	public UserAction(String userId, Category category, Action action) {
		this.userId = userId;
		this.category = category;
		this.action = action;
		this.timestamp = LocalDateTime.now();
	}
}
