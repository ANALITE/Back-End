package eci.analite.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import eci.analite.data.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	User findByUsername(String username);
	void deleteByUsername(String username);
	boolean existsByUsername(String username);
}
