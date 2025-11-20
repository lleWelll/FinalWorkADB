package org.tech.finalprojectadb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.User;
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

	private final StringRedisTemplate redisTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();


	/* ----------------- GET ----------------- */

	public Optional<Product> getProductCache(String productId) {
		String key = generateProductKey(productId);
		Optional<Product> product = Optional.ofNullable(productRedisTemplate.opsForValue().get(key));
		log.info("CACHE: Getting key: {} from cache: {}", key, product);
		return product;
	}

	public List<Product> getAllProductCache() {
		String key = generateAllProductKey();
		String json = redisTemplate.opsForValue().get(key);

		try {
			if (json == null) {
				return null;
			}
			log.info("CACHE: Getting key: {} from cache: {}", key, json);
			return objectMapper.readValue(json, new TypeReference<List<Product>>() {});
		} catch (JsonProcessingException e) {
			log.error("Error occured  while parsing: {}", e.getMessage());
			throw new RuntimeException(e);
		}
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

	public Optional<User> getUserCache(String identifier, String identifierType) {
		String key;
		if (identifierType.equals("username")) {
			key = generateUserCacheByName(identifier);
		} else {
			key = generateUserCacheById(identifier);
		}

		String json = redisTemplate.opsForValue().get(key);
		log.info("CACHE: Getting key: {} from cache", key);

		try {
			if (json == null) {
				return Optional.empty();
			}
			return Optional.of(objectMapper.readValue(json, User.class));
		} catch (JsonProcessingException e) {
			log.error("Error occured  while parsing: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}



	/* ----------------- SET ----------------- */

	public List<Product> setAndReturnAllProductCache(List<Product> allProducts) {
		String key = generateAllProductKey();
		log.info("CACHE: Setting new key: {} to cache", key);

		try {
			String json = objectMapper.writeValueAsString(allProducts);
			redisTemplate.opsForValue().set(key, json, 30, TimeUnit.MINUTES);
			return allProducts;
		} catch (JsonProcessingException e) {
			log.error("Error occured  while parsing: {}", e.getMessage());
			throw new RuntimeException(e);
		}
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

	public User setAndReturnUserCache(User user, String identifierType) {
		String key;
		if (identifierType.equals("username")) {
			key = generateUserCacheByName(user.getUsername());
		} else {
			key = generateUserCacheById(user.getId());
		}

		log.info("CACHE: Setting new key: {} to cache", key);
		try {
			String json = objectMapper.writeValueAsString(user);
			redisTemplate.opsForValue().set(key, json, 5, TimeUnit.MINUTES);
			return user;
		} catch (JsonProcessingException e) {
			log.error("Error occured  while parsing: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}



	/* ----------------- KEYS ----------------- */

	private String generateProductKey(String id) {
		final String key = "products:";
		return key + id;
	}

	private String generateAllProductKey() {
		return "products:all";
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

	private String generateUserCacheByName(String username) {
		return "users:username:" + username;
	}

	private String generateUserCacheById(String id) {
		return "users:id:" + id;
	}
}
