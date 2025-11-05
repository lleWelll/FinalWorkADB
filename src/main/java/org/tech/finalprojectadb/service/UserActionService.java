package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.LikeActionDuplicateEntityException;
import org.tech.finalprojectadb.repository.UserActionRepository;
import org.tech.finalprojectadb.util.Action;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActionService {

	private final UserActionRepository userActionRepository;

	private final UserService userService;

	public List<UserAction> getAll() {
		String userId = userService.getCurrentUserId();
		return userActionRepository.findAllByUserIdOrderByTimestampDesc(userId);
	}

	public List<UserAction> filterByAction(List<Action> actions) {
		String userId = userService.getCurrentUserId();
		return userActionRepository.findAllByUserIdAndActionInOrderByTimestampDesc(userId, actions);
	}

	public void addViewAction(Product product) {
		addAction(product, Action.VIEW);
	}

	/**
	 * Может быть добавлен только одна LIKE запись для конкретного продукта и для конкретного пользователя
	 */
	public void addLikeAction(Product product) throws LikeActionDuplicateEntityException {
		addAction(product, Action.LIKE);
	}

	/**
	 * Предпологается, что может существовать только одна LIKE запись для конкретного продукта и для конкретного пользователя
	 */
	public void removeLikeAction(Product product) throws LikeActionDuplicateEntityException, EntityNotFoundException {
		String userId = userService.getCurrentUserId();
		Optional<UserAction> actionOptional = userActionRepository.findByUserIdAndProductIdAndAction(userId, product.getId(), Action.LIKE);
		if (actionOptional.isEmpty()) {
			log.warn("{} User Action for user: {} and product: {} not found, can't delete action", Action.LIKE, userId, product.getId());
			throw new EntityNotFoundException(Action.LIKE + " User Action for user: " + userId + " and product: " + product.getId() + " not found");
		}

		UserAction action = actionOptional.get();
		removeAction(action);
	}

	public void addPurchaseAction(Product product) {
		addAction(product, Action.PURCHASE);
	}

	private void addAction(Product product, Action actionType) {
		String userId = userService.getCurrentUserId();

		// LIKE запись не будет добавлена если такая же запись уже существует
		if (actionType.equals(Action.LIKE) &&
				isExistsAction(userId, product.getId(), actionType)) {
			log.info("LIKE action for product: {} and user: {} exists, can not add new {} action", product.getId(), userId, actionType);
			throw new LikeActionDuplicateEntityException("LIKE action for product: " + product.getId() + " and user: " + userId + " exists, can not add new " + actionType + " action");
		}

		UserAction action = new UserAction(userId, product.getId(), actionType);
		userActionRepository.save(action);
		log.info("LIKE action successfully added: product: {}, user: {}", product.getId(), userId);
	}

	private boolean isExistsAction(String userId, String productId, Action actionType) {
		return userActionRepository.existsByUserIdAndProductIdAndAction(userId, productId, actionType);
	}

	private void removeAction(UserAction action) throws EntityNotFoundException {
		if (! userActionRepository.existsById(action.getId())) {
			log.error("User Action: {} not found", action.getId());
			throw new EntityNotFoundException("User Action: " + action.getId() + " not found");
		}

		userActionRepository.delete(action);
		log.info("User Action successfully deleted: Action: {}", action);
	}

}
