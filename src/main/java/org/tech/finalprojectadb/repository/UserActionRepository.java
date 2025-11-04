package org.tech.finalprojectadb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.UserAction;
import org.tech.finalprojectadb.util.Action;

import java.util.List;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {

	List<UserAction> findAllByUserId(String userId);

	List<UserAction> findAllByUserIdAndActionIn(String userId, List<Action> actions);

}
