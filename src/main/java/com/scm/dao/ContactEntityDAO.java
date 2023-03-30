package com.scm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scm.entity.ContactEntity;

public interface ContactEntityDAO extends JpaRepository<ContactEntity, Integer> {
	
	@Query("from ContactEntity c where c.user.id =:userId")
	public Page<ContactEntity> findContactsByUserId(@Param("userId")int userId, Pageable pageable);
	
	@Query("from ContactEntity c where (lower(c.name) like lower(concat('%', :search, '%')) " + 
	"or lower(c.secondName) like lower(concat('%', :search, '%')) " +
	"or lower(c.work) like lower(concat('%', :search, '%')) " +
	"or lower(c.email) like lower(concat('%', :search, '%')) " +
	"or lower(c.phone) like lower(concat('%', :search, '%'))) and c.user.id =:userId")
	public List<ContactEntity> findByColumn(@Param("search")String search, @Param("userId") int userId);
	
	
}
