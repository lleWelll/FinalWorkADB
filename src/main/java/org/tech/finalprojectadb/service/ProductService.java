package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.LikeActionDuplicateEntityException;
import org.tech.finalprojectadb.repository.ProductRepository;
import org.tech.finalprojectadb.util.Category;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final ProductRepository productRepository;

	private final UserService userService;

	private final UserActionService userActionService;

	private final CacheService cacheService;

	public String likeProduct(String id) {
		Product product = getProductById(id);

		try {
			userActionService.addLikeAction(userService.getCurrentUserId(), product);
			return "Product '" + product + "' Liked";
		} catch (LikeActionDuplicateEntityException e) {
			return "Like already exists for this product: " + product;
		}
	}

	public void removeLike(String id) {
		Product product = getProductById(id);

		try {
			userActionService.removeLikeAction(userService.getCurrentUserId(), product);
			log.info("Like successfully removed from product: {}", product);
		} catch (EntityNotFoundException e) {
			log.info("There is no like on product: {}", product);
		}
	}

	public String purchaseProduct(String id) {
		Product product = getProductById(id);
		userActionService.addPurchaseAction(userService.getCurrentUserId(), product);
		return "Product '" + product + "' Purchased";
	}

	public Product findProductById(String id) {
		Product product = getProductById(id);
		userActionService.addViewAction(userService.getCurrentUserId(), product);
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
		String userId = userService.getCurrentUserId();
		categories.forEach((cat) -> userActionService.addViewAction(userId, cat));

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
		} else if (start.isPresent() && end.isPresent()) {
			return productRepository.findProductByPriceIsBetween(start.get(), end.get());
		} else {
			return getAll();
		}
	}

	private Product getProductById(String id) {
		return cacheService.getProductCache(id).orElseGet(
				() -> {
					Product pr = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product: " + id + " is not found"));
					return cacheService.setAndReturnProductCache(pr);
				}
		);
	}
}
