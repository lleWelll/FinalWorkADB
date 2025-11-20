package org.tech.finalprojectadb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.tech.finalprojectadb.entity.Product;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.exceptions.EntityNotFoundException;
import org.tech.finalprojectadb.exceptions.LikeActionDuplicateEntityException;
import org.tech.finalprojectadb.repository.UserActionRepository;
import org.tech.finalprojectadb.util.Action;
import org.tech.finalprojectadb.util.Category;
import org.tech.finalprojectadb.util.UserActionFullInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActionService {

	private final UserActionRepository userActionRepository;

	private List<UserAction> batch = new ArrayList<>(100);

	private Date lastBatch = new Date();

	public List<UserAction> getAll(String userId) {
		return userActionRepository.findAllByUserIdOrderByTimestampDesc(userId);
	}

	public List<UserAction> filterByAction(String userId, List<Action> actions) {
		return userActionRepository.findAllByUserIdAndActionInOrderByTimestampDesc(userId, actions);
	}

	public List<UserAction> filterByUserIdAndLimit(String userId, int limit) {
		return userActionRepository.findAllByUserIdOrderByTimestampDesc(userId, Limit.of(limit));
	}

	public List<UserActionFullInfo> getUserActionsAndProductsByUserId(String userId) {
		return userActionRepository.findUserActionFullInfoByUserId(userId);
	}

	/* ------------------------ By Product ------------------------ */

	public void addViewAction(String userId, Product product) {
		addAction(userId, product, Action.VIEW);
	}

	/**
	 * Может быть добавлен только одна LIKE запись для конкретного продукта и для конкретного пользователя
	 */
	public void addLikeAction(String userId, Product product) throws LikeActionDuplicateEntityException {
		addAction(userId, product, Action.LIKE);
	}

	/**
	 * Предпологается, что может существовать только одна LIKE запись для конкретного продукта и для конкретного пользователя
	 */
	public void removeLikeAction(String userId, Product product) throws LikeActionDuplicateEntityException, EntityNotFoundException {
		Optional<UserAction> actionOptional = userActionRepository.findByUserIdAndProductIdAndAction(userId, product.getId(), Action.LIKE);
		if (actionOptional.isEmpty()) {
			log.warn("{} User Action for user: {} and product: {} not found, can't delete action", Action.LIKE, userId, product.getId());
			throw new EntityNotFoundException(Action.LIKE + " User Action for user: " + userId + " and product: " + product.getId() + " not found");
		}

		UserAction action = actionOptional.get();
		removeAction(action);
	}

	public void addPurchaseAction(String userId, Product product) {
		addAction(userId, product, Action.PURCHASE);
	}


	/* ------------------------ Private ------------------------ */

	private void addAction(String userId, Product product, Action actionType) {
		// LIKE запись не будет добавлена если такая же запись уже существует
		if (actionType == Action.LIKE &&
				isExistsAction(userId, product.getId(), actionType)) {
			log.info("LIKE action for product: {} and user: {} exists, can not add new {} action", product.getId(), userId, actionType);
			throw new LikeActionDuplicateEntityException("LIKE action for product: " + product.getId() + " and user: " + userId + " exists, can not add new " + actionType + " action");
		}
		UserAction action = new UserAction(userId, product.getId(), actionType);
		batch.add(action);
		log.info("Adding {} to batch. Current size: {}", product, batch.size());

		Date current = new Date();
		if (current.getTime() - lastBatch.getTime() >= 1000) {
			userActionRepository.insert(batch);
			lastBatch = current;
			log.info("Inserting batch with size {} in user_actions", batch.size());
			batch = new ArrayList<>(100);
		}
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
