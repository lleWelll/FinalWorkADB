package org.tech.finalprojectadb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.service.ProductService;
import org.tech.finalprojectadb.util.Category;

import java.util.List;
import java.util.Optional;

@Tag(name = "ProductController", description = "Операции над продуктами")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "Получение всех продуктов")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Возвращает список всех существующих продуктов"),
	})
	@GetMapping("/")
	public ResponseEntity<List<Product>> getAll() {
		return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
	}

	@Operation(summary = "Лайк продукта")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Лайкает продукт. Добавляет новую 'LIKE' запись в историю пользователя"),
			@ApiResponse(responseCode = "404", description = "Указанный продукт не существует")
	})
	@PostMapping("/{id}/like")
	public ResponseEntity<String> likeProductById(
			@Parameter(name = "id", description = "id продукта") @PathVariable String id) {
		return new ResponseEntity<>(productService.likeProduct(id), HttpStatus.OK);
	}

	@Operation(summary = "Удаление лайка")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Лайк для указанного продукта не существует. Существующая 'LIKE' запись удаляется")
	})
	@DeleteMapping("/{id}/like")
	public ResponseEntity<String> deleteProductById(
			@Parameter(name = "id", description = "id продукта") @PathVariable String id) {
		productService.removeLike(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(
			summary = "Покупка продукта",
			description = "Совершает покупку продукта и добавляет 'PURCHASE' запись в историю пользователя"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Покупка успешно завершена"),
			@ApiResponse(responseCode = "404", description = "Указанный продукт не найден")
	})
	@PostMapping("/{id}/buy")
	public ResponseEntity<String> buyProductById(
			@Parameter(name = "id", description = "id продукта")  @PathVariable String id) {
		return new ResponseEntity<>(productService.purchaseProduct(id), HttpStatus.OK);
	}

	@Operation(
			summary = "Получение продукта по ID",
			description = "Возвращает продукт с указанным ID. Присутствует побочный эффект - добавляется 'VIEW' запись в историю пользователя"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Продукт успешно найден, новая 'VIEW' запись успешно добавлена"),
			@ApiResponse(responseCode = "404", description = "Продукт не найден")
	})
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(
			@Parameter(name = "id", description = "id продукта")   @PathVariable String id) {
		return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
	}

	@Operation(
			summary = "Фильтрация по бренду",
			description = "Возвращает список продуктов, отфильтрованных по брендам"
	)
	@ApiResponse(responseCode = "200", description = "Список продуктов, соответствующих фильтру")
	@GetMapping("/by-brand")
	public ResponseEntity<List<Product>> getAllByBrand(
			@Parameter(name = "brand", description = "Список брендов для фильтрации") @RequestParam List<String> brand) {
		return new ResponseEntity<>(productService.filterByBrand(brand), HttpStatus.OK);
	}

	@Operation(
			summary = "Фильтрация по модели",
			description = "Возвращает список продуктов, отфильтрованных по моделям"
	)
	@ApiResponse(responseCode = "200", description = "Список продуктов, соответствующих фильтру")
	@GetMapping("/by-model")
	public ResponseEntity<List<Product>> getAllByModel(
			@Parameter(name = "model", description = "Список моделей для фильтрации") @RequestParam List<String> model) {
		return new ResponseEntity<>(productService.filterByModel(model), HttpStatus.OK);
	}

	@Operation(
			summary = "Фильтрация по категории",
			description = "Возвращает список продуктов, отфильтрованных по категориям"
	)
	@ApiResponse(responseCode = "200", description = "Список продуктов, соответствующих фильтру")
	@GetMapping("/by-category")
	public ResponseEntity<List<Product>> getAllByCategory(
			@Parameter(name = "category", description = "Список категорий для фильтрации") @RequestParam List<Category> category) {
		return new ResponseEntity<>(productService.filterByCategory(category), HttpStatus.OK);
	}

	@Operation(
			summary = "Фильтрация по цене",
			description = "Возвращает список продуктов в диапазоне цен"
	)
	@ApiResponse(responseCode = "200", description = "Список продуктов в заданном диапазоне цен")
	@GetMapping("/by-price")
	public ResponseEntity<List<Product>> getAllBetweenPrice(
			@Parameter(name = "start", description = "Минимальная цена") @RequestParam(required = false) Optional<Integer> start,
			@Parameter(name = "end", description = "Максимальная цена") @RequestParam(required = false) Optional<Integer> end) {
		return new ResponseEntity<>(productService.filterByPrice(start, end), HttpStatus.OK);
	}
}
