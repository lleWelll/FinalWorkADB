package org.tech.finalprojectadb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.tech.finalprojectadb.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

	User findUserByUsername(String username);

	boolean existsUserByUsername(String username);
}
