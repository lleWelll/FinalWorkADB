package org.tech.finalprojectadb.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.util.Action;
import org.tech.finalprojectadb.util.Category;
import org.tech.finalprojectadb.util.UserActionFullInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {

	@Aggregation(pipeline = {
			"{ $match: { user_id: ?0 } }",
			"{ $lookup: { " +
					"from: 'products', " +
					"let: { pid: { $toObjectId: '$product_id' } }, " +
					"pipeline: [ " +
					"{ $match: { $expr: { $eq: ['$_id', '$$pid'] } } } " +
					"], " +
					"as: 'product' " +
					"} " +
			"}",
			"{ $unwind: '$product' }" +
			"{ $project: { " +
					"action: '$action', " +
					"product: '$product' " +
					"} " +
			"}"
	})
	List<UserActionFullInfo> findUserActionFullInfoByUserId(String userId);

	List<UserAction> findAllByUserIdOrderByTimestampDesc(String userId);

	List<UserAction> findAllByUserIdAndActionInOrderByTimestampDesc(String userId, List<Action> actions);

	List<UserAction> findAllByUserIdOrderByTimestampDesc(String type, Limit limit);

	Optional<UserAction> findByUserIdAndProductIdAndAction(String userId, String productId, Action action);

	Optional<UserAction> findByUserIdAndCategoryAndAction(String userId, Category category, Action action);

	boolean existsByUserIdAndProductIdAndAction(String userId, String productId, Action action);

	boolean existsByUserIdAndCategoryAndAction(String userId, Category category, Action action);

}
