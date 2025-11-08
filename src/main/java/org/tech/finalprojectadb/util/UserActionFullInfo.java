package org.tech.finalprojectadb.util;

import lombok.Data;
import org.tech.finalprojectadb.entity.Product;

@Data
public class UserActionFullInfo {

	private Action action;

	private Product product;
}
