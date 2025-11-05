package org.tech.finalprojectadb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.util.Action;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {

	List<UserAction> findAllByUserIdOrderByTimestampDesc(String userId);

	List<UserAction> findAllByUserIdAndActionInOrderByTimestampDesc(String userId, List<Action> actions);

	Optional<UserAction> findByUserIdAndProductIdAndAction(String userId, String productId, Action action);

	boolean existsByUserIdAndProductIdAndAction(String userID, String productId, Action action);

}
