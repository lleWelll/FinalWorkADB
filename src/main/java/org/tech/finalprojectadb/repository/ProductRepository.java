package org.tech.finalprojectadb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.util.Category;

import java.util.List;

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

}
