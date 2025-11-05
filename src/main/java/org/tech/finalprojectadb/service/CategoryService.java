package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.LikeActionDuplicateEntityException;
import org.tech.finalprojectadb.util.Category;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

	private final UserActionService userActionService;

	public String getAll() {
		return Arrays.toString(Category.values());
	}

	public String likeCategory(Category category) {
		try {
			userActionService.addLikeAction(category);
			return "Category '" + category + "' Liked";
		} catch (LikeActionDuplicateEntityException e) {
			return "Like already exists for this category: " + category;
		}
	}

	public void removeCategory(Category category) {
		try {
			userActionService.removeLikeAction(category);
			log.info("Like successfully removed from category: {}", category);
		} catch (EntityNotFoundException e) {
			log.info("There is no like on category: {}", category);
		}
	}
}
