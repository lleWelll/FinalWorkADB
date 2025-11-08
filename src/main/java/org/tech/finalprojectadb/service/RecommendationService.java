package org.tech.finalprojectadb.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.MathArrays;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.util.Category;
import org.tech.finalprojectadb.util.UserActionFullInfo;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
	/*
	Рекомендации для конкретного пользователя
	1. Сделать эмбеддинг продуктов (one-shot)
	2. Суммировать веса действий над продуктами
	3. Посчитать профиль пользователя (3A * 5A + 1*C)
	4. Узнать симиларити для каждого продукта
	5. Отсортировать K схожих продуктов
	6. Вывести список
	 */

	private final UserActionService userActionService;

	private final ProductService productService;

	public List<Product> getRecommendedProducts(String userId, Integer limit) {
		int maxPrice = productService.getMostExpensiveProduct().getPrice();
		String[] allBrands = productService.getAllUniqueBrands().toArray(String[]::new);


		List<UserActionFullInfo> userActionsAndProduct = userActionService.getUserActionsAndProductsByUserId(userId);
		List<ProductEmbedding> productEmbeddings = userActionsAndProduct.stream()
				.map(userActionFullInfo -> {
					ProductEmbedding productEmbedding = new ProductEmbedding();
					productEmbedding.productId = userActionFullInfo.getProduct().getId();
					productEmbedding.embedding = embedProduct(userActionFullInfo.getProduct(), allBrands, Category.values(), maxPrice);
					productEmbedding.weight = userActionFullInfo.getAction().getWeight();
					return productEmbedding;
				})
				.toList();

		Map<String, ProductEmbedding> uniqueEmbedding = getUniqueEmbeddings(productEmbeddings);
		double[] userProfile = calculateUserProfile(uniqueEmbedding, productEmbeddings.get(0).embedding.length);

		List<Product> allProducts = productService.getAll();
		Map<String, Double> similarityTable = new HashMap<>(); // [ProductId - similarityScore]
		for (Product product : allProducts) {
			double[] embedding = embedProduct(product, allBrands, Category.values(), maxPrice);
			similarityTable.put(product.getId(), calculateCosineSimilarity(userProfile, embedding));
		}

		if (limit == null) {
			limit = 10;
		}

		return allProducts.stream()
				.filter(p -> similarityTable.containsKey(p.getId()))
				.sorted(Comparator.comparingDouble(
								(Product p) -> similarityTable.getOrDefault(p.getId(), -1d))
						.reversed())
				.limit(limit)
				.toList();
	}


	private double[] embedProduct(Product product, String[] allBrands, Category[] allCategories, int maxProductPrice) {
		double[] brandOneHot = new double[allBrands.length];
		for (int i = 0; i < allBrands.length; i++) {
			if (allBrands[i].equals(product.getBrand())) {
				brandOneHot[i] = 1d;
				break;
			} else {
				brandOneHot[i] = 0d;
			}
		}

		double[] categoryOneHot = new double[allCategories.length];
		for (int i = 0; i < allCategories.length; i++) {
			if (allCategories[i] == product.getCategory()) {
				categoryOneHot[i] = 1d;
				break;
			} else {
				categoryOneHot[i] = 0d;
			}
		}

		double[] normalizedPrice = new double[] {(double) product.getPrice() / maxProductPrice};

		return MathArrays.concatenate(brandOneHot, categoryOneHot, normalizedPrice);
	}

	private Map<String, ProductEmbedding> getUniqueEmbeddings(List<ProductEmbedding> embeddings) {
		Map<String, ProductEmbedding> uniqueEmbeddings = new HashMap<>();
		for (ProductEmbedding embedding : embeddings) {
			if (uniqueEmbeddings.containsKey(embedding.productId)) {
				embedding.weight = uniqueEmbeddings.get(embedding.productId).weight + embedding.weight;
				uniqueEmbeddings.put(embedding.productId, embedding);
			} else {
				uniqueEmbeddings.put(embedding.productId, embedding);
			}
		}
		return uniqueEmbeddings;
	}

	private double[] calculateUserProfile(Map<String, ProductEmbedding> embeddings, int embeddingSize) {
		double[] userProfile = new double[embeddingSize];
		for (ProductEmbedding productEmbedding : embeddings.values()) {
			double[] scale = MathArrays.scale(productEmbedding.weight, productEmbedding.embedding);
			userProfile = MathArrays.ebeAdd(userProfile, scale);
		}

		return userProfile;
	}

	private double calculateCosineSimilarity(double[] pointA, double[] pointB) {
		return MathArrays.cosAngle(pointA, pointB);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class ProductEmbedding {
		String productId;
		double[] embedding;
		double weight;

	}

}
