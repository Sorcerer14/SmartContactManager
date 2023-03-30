package com.scm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.configurations.CustomUserDetails;
import com.scm.dao.UserEntityDAO;
import com.scm.entity.UserEntity;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserEntityDAO userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity getUser=userDao.getUserByUserName(username);
		
		if(getUser==null) {
			throw new UsernameNotFoundException("Could not find the user!!");
		}
		
		CustomUserDetails customUserDetails=new CustomUserDetails(getUser);
		
		return customUserDetails;
	}
	

}
