package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.UserAction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

	private final RedisTemplate<String, Product> productRedisTemplate;

	private final RedisTemplate<String, UserAction> userActionRedisTemplate;

	private final RedisTemplate<String, List<String>> generalRedisTemplate;

	public Optional<Product> getProductCache(String productId) {
		String key = generateProductKey(productId);
		Optional<Product> product = Optional.ofNullable(productRedisTemplate.opsForValue().get(key));
		log.info("CACHE: Getting key: {} from cache: {}", key, product);
		return product;
	}

	public Optional<Product> getMostExpensiveProductCache() {
		String key = generateMostExpensiveProductKey();
		Optional<Product> product = Optional.ofNullable(productRedisTemplate.opsForValue().get(key));
		log.info("CACHE: Getting key: {} from cache: {}", key, product);
		return product;
	}

	public List<String> getAllBrandsCache() {
		String key = generateBrandsKey();

		List<String> result = generalRedisTemplate.opsForValue().get(key);
		log.info("CACHE: Getting key: {} from cache", key);
		return result;
	}

	public List<String> setAndReturnAllBrandsCache(List<String> allBrands) {
		String key = generateBrandsKey();
		log.info("CACHE: Setting new key: {} to cache", key);
		generalRedisTemplate.opsForValue().set(key, allBrands, 5, TimeUnit.MINUTES);
		return allBrands;
	}

	public Product setAndReturnProductCache(Product product) {
		String key = generateProductKey(product.getId());
		log.info("CACHE: Setting new key: {} to cache", key);
		productRedisTemplate.opsForValue().set(key, product, 5, TimeUnit.MINUTES);
		return product;
	}

	public Product setAndReturnMostExpensiveProductCache(Product product) {
		String key = generateMostExpensiveProductKey();
		log.info("CACHE: Setting new key: {} to cache", key);
		productRedisTemplate.opsForValue().set(key, product, 5, TimeUnit.MINUTES);
		return product;
	}

	public Optional<UserAction> getUserActionCache(String actionId) {
		String key = generateProductKey(actionId);
		Optional<UserAction> userAction = Optional.ofNullable(userActionRedisTemplate.opsForValue().get(key));
		log.info("CACHE: Getting key: {} from cache: {}", key, userAction);
		return userAction;
	}

	public UserAction setAndReturnUserActionCache(UserAction userAction) {
		String key = generateUserActionKey(userAction.getId());
		log.info("CACHE: Setting new key: {} to cache", key);
		userActionRedisTemplate.opsForValue().set(key, userAction, 5, TimeUnit.MINUTES);
		return userAction;
	}


	private String generateProductKey(String id) {
		final String key = "products:";
		return key + id;
	}

	private String generateMostExpensiveProductKey() {
		return  "products:max_price";
	}

	private String generateUserActionKey(String id) {
		final String key = "user_actions:";
		return key + id;
	}

	private String generateBrandsKey() {
		return "products:all_brands";
	}
}
