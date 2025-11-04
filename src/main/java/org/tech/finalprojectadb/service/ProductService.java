package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.repository.ProductRepository;
import org.tech.finalprojectadb.repository.UserActionRepository;
import org.tech.finalprojectadb.util.Category;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	private final UserActionService userActionService;

	public Product getProductById(String id) {
		Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		userActionService.addViewAction(product);
		return product;
	}

	public List<Product> getAll() {
		return productRepository.findAll();
	}

	public List<Product> filterByBrand(List<String> brands) {
		if (brands.size() > 1) {
			return productRepository.findProductByBrandIn(brands);
		} else {
			return productRepository.findProductByBrand(brands.get(0));
		}
	}

	public List<Product> filterByModel(List<String> models) {
		if (models.size() > 1) {
			return productRepository.findProductByModelIn(models);
		} else {
			return productRepository.findProductByModel(models.get(0));
		}
	}

	public List<Product> filterByCategory(List<Category> categories) {
		if (categories.size() > 1) {
			return productRepository.findProductByCategoryIn(categories);
		} else {
			return productRepository.findProductByCategory(categories.get(0));
		}
	}

	public List<Product> filterByPrice(Optional<Integer> start, Optional<Integer> end) {
		if (start.isPresent() && end.isEmpty()) {
			return productRepository.findProductByPriceIsBetween(start.get(), Integer.MAX_VALUE);
		} else if (start.isEmpty() && end.isPresent()) {
			return productRepository.findProductByPriceIsBetween(0, end.get());
		} else if (start.isPresent() && end.isPresent()){
			return productRepository.findProductByPriceIsBetween(start.get(), end.get());
		} else {
			return getAll();
		}
	}
}
