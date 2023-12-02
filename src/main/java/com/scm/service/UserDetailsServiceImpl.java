package com.scm.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.entity.UserEntity;
import com.scm.repository.UserEntityDAO;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserEntityDAO userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> getUser=userDao.getUserByUserName(username);
		
		if(!getUser.isPresent()) {
			throw new UsernameNotFoundException("Could not find the user!!");
		}
		
		return new User(getUser.get().getEmail(), getUser.get().getPassword(), Arrays.asList(new SimpleGrantedAuthority(getUser.get().getRole())));
	}
	

}
