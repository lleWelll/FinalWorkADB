package org.tech.finalprojectadb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.service.ProductService;
import org.tech.finalprojectadb.util.Category;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/")
	public List<Product> getAll() {
		return productService.getAll();
	}

	@GetMapping("/by-brand")
	public List<Product> getAllByBrand(@RequestParam List<String> brand) {
		return productService.filterByBrand(brand);
	}

	@GetMapping("/by-model")
	public List<Product> getAllByModel(@RequestParam List<String> model) {
		return productService.filterByModel(model);
	}

	@GetMapping("/by-category")
	public List<Product> getAllByCategory(@RequestParam List<Category> category) {
		return productService.filterByCategory(category);
	}

	@GetMapping("/by-price")
	public List<Product> getAllBetweenPrice(@RequestParam(required = false) Optional<Integer> start,
											@RequestParam(required = false) Optional<Integer> end) {
		return productService.filterByPrice(start, end);
	}

	@GetMapping("/{id}")
	public Product getProductById(@PathVariable String id) {
		return productService.getProductById(id);
	}

}
