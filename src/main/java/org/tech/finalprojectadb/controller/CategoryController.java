package org.tech.finalprojectadb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tech.finalprojectadb.service.CategoryService;
import org.tech.finalprojectadb.util.Category;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/")
	public ResponseEntity<String> getAll() {
		return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
	}

	@PostMapping("/{category}/like")
	public ResponseEntity<String> likeCategory(@PathVariable String category) {
		try {
			Category cat = Category.valueOf(category.toUpperCase());
			return new ResponseEntity<>(categoryService.likeCategory(cat), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>("Category '" + category + "' Not Found", HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{category}/like")
	public ResponseEntity<String> removeLikeFromCategory(@PathVariable String category) {
		try {
			Category cat = Category.valueOf(category.toUpperCase());
			categoryService.removeCategory(cat);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>("Category '" + category + "' Not Found", HttpStatus.BAD_REQUEST);
		}
	}

}
