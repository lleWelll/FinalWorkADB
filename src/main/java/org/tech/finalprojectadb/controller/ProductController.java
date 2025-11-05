package org.tech.finalprojectadb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<Product>> getAll() {
		return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
	}

	@PostMapping("/{id}/like")
	public ResponseEntity<String> likeProductById(@PathVariable String id) {
		return new ResponseEntity<>(productService.likeProduct(id), HttpStatus.OK);
	}

	@DeleteMapping("/{id}/like")
	public ResponseEntity<String> deleteProductById(@PathVariable String id) {
		productService.removeLike(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/{id}/buy")
	public ResponseEntity<String> buyProductById(@PathVariable String id) {
		return new ResponseEntity<>(productService.purchaseProduct(id), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable String id) {
		return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
	}

	@GetMapping("/by-brand")
	public ResponseEntity<List<Product>> getAllByBrand(@RequestParam List<String> brand) {
		return new ResponseEntity<>(productService.filterByBrand(brand), HttpStatus.OK);
	}

	@GetMapping("/by-model")
	public ResponseEntity<List<Product>> getAllByModel(@RequestParam List<String> model) {
		return new ResponseEntity<>(productService.filterByModel(model), HttpStatus.OK);
	}

	@GetMapping("/by-category")
	public ResponseEntity<List<Product>> getAllByCategory(@RequestParam List<Category> category) {
		return new ResponseEntity<>(productService.filterByCategory(category), HttpStatus.OK);
	}

	@GetMapping("/by-price")
	public ResponseEntity<List<Product>> getAllBetweenPrice(@RequestParam(required = false) Optional<Integer> start,
											@RequestParam(required = false) Optional<Integer> end) {
		return new ResponseEntity<>(productService.filterByPrice(start, end), HttpStatus.OK);
	}
}
