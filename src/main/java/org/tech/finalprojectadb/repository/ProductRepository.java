package org.tech.finalprojectadb.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.util.Category;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

	List<Product> findProductByBrand(String brand);

	List<Product> findProductByBrandIn(List<String> brands);

	List<Product> findProductByModel(String model);

	List<Product> findProductByModelIn(List<String> models);

	List<Product> findProductByBrandAndModel(String brand, String model);

	List<Product> findProductByCategory(Category category);

	List<Product> findProductByCategoryIn(List<Category> categories);

	List<Product> findProductByPriceIsBetween(Integer start, Integer end);

	List<Product> findProductsByIdIn(Set<String> id);

	Product findTopByOrderByPriceDesc();

	@Aggregation(pipeline = {
			"{ $match: { brand: { $type: 'string', $ne: '' } } }",
			"{ $project: { brand: { $trim: { input: '$brand' } } } }",
			"{ $group: { _id: '$brand' } }",
			"{ $sort: { _id: 1 } }"
	})
	List<String> findAllUniqueBrands();

}
