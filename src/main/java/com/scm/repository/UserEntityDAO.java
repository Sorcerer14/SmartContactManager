package com.scm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entity.UserEntity;

@Repository
public interface UserEntityDAO extends JpaRepository<UserEntity, Integer> {

	@Query("select u from UserEntity u where u.email = :email")
	public Optional<UserEntity> getUserByUserName(@Param("email") String email);
}
