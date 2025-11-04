package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.repository.UserActionRepository;
import org.tech.finalprojectadb.util.Action;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserActionService {

	private final UserActionRepository userActionRepository;

	private final UserService userService;

	public List<UserAction> getAll() {
		String userId = userService.getCurrentUserId();
		return userActionRepository.findAllByUserId(userId);
	}

	public List<UserAction> filterByAction(List<Action> actions) {
		String userId = userService.getCurrentUserId();
		return userActionRepository.findAllByUserIdAndActionIn(userId, actions);
	}

	public void addViewAction(Product product) {
		addAction(product, Action.VIEW);
	}

	public void addLikeAction(Product product) {
		addAction(product, Action.LIKE);
	}

	public void addPurchaseAction(Product product) {
		addAction(product, Action.PURCHASE);
	}

	private void addAction(Product product, Action actionType) {
		String userId = userService.getCurrentUserId();
		UserAction action = new UserAction(userId, product.getId(), actionType);

		userActionRepository.save(action);
	}

}
