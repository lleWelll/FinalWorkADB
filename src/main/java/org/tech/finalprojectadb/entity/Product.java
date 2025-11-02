package org.tech.finalprojectadb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.tech.finalprojectadb.util.Category;

@Document(collection = "products")
@Data
public class Product {

	@Id
	private String id;

	@Field(name = "brand")
	private String brand;

	@Field(name = "model")
	private String model;

	@Field(name = "price")
	private Integer price;

	@Field(name = "category")
	private Category category;

}
