package com.scm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.business.bean.ContactBean;
import com.scm.business.bean.UserBean;
import com.scm.service.ScmService;

@RestController
@RequestMapping("/user")
public class SearchController {
	
	@Autowired
	private ScmService service;
	
	//Search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		String username=principal.getName();
		UserBean user=service.getUserByUsername(username);
		List<ContactBean> result=service.searchContact(query, user);
		
		return ResponseEntity.ok(result);
	}
}
