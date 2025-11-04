package org.tech.finalprojectadb.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.tech.finalprojectadb.util.Action;

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

	private Action action;

	public UserAction(String userId, String productId, Action action) {
		this.userId = userId;
		this.productId = productId;
		this.action = action;
	}
}
