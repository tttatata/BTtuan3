package vn.btthtuan4pttk.demo.jwt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.btthtuan4pttk.demo.jwt.entities.User;

public interface UserRepository  extends JpaRepository<User,  Long>{
	
Optional<User> findByUsername(String username);
Boolean existByUsername(String username);
Boolean existByEmail(String email);
}
