package com.scm.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scm.business.bean.ContactBean;
import com.scm.business.bean.UserBean;
import com.scm.entity.ContactEntity;
import com.scm.entity.UserEntity;
import com.scm.repository.ContactEntityDAO;
import com.scm.repository.UserEntityDAO;


@Service
public class ScmService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	ResourceLoader resourceLoader; 
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserEntityDAO userDao;
	
	@Autowired
	private ContactEntityDAO contactDao;
	
	public int saveUser(UserBean userBean, MultipartFile image) throws Exception {
		userBean.setStatus(true);
		userBean.setRole("ROLE_USER");
		
		if(!image.isEmpty()) {
			Path path=getPath(image);
			userBean.setImageUrl(path.toString().substring(path.toString().lastIndexOf("\\") + 1));
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			userBean.setImageUrl("default.png");
		}
		
		UserEntity userEntity=modelMapper.map(userBean, UserEntity.class);
		
		
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		
		UserEntity result = userDao.save(userEntity);
		
		return result.getId();
	}
	
	public int updateUser(UserBean userBean, MultipartFile image) throws Exception {
		if(!image.isEmpty()) {
			Resource filePath= resourceLoader.getResource("classpath:static/img/"+userBean.getImageUrl());
			if(filePath.exists() && !image.getOriginalFilename().equals("default.png")) {
				filePath.getFile().delete();
			}
			Path path=getPath(image);
			userBean.setImageUrl(path.toString().substring(path.toString().lastIndexOf("\\") + 1));
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		
		UserEntity userEntity=modelMapper.map(userBean, UserEntity.class);
		
		UserEntity result = userDao.save(userEntity);
		
		return result.getId();
	}
	
	private Path getPath(MultipartFile image) throws Exception {
		File imageFile= new ClassPathResource("static/img").getFile();
		Random random=new Random();
		String randomizer=String.format("%s%s",System.currentTimeMillis(),random.nextInt(100000));
		String fileName=image.getOriginalFilename().substring(0,image.getOriginalFilename().lastIndexOf("."));
		String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
		Path path=Paths.get(imageFile.getAbsolutePath()+File.separator+fileName+randomizer+"."+extension);
		return path;
	}
	
	public int save(ContactBean contactBean, MultipartFile image) throws Exception {
		if(!image.isEmpty()) {
			Path path=getPath(image);
			contactBean.setImage(path.toString().substring(path.toString().lastIndexOf("\\") + 1));
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			contactBean.setImage("default.png");
		}
		
		UserBean userBean=contactBean.getUser();
		UserEntity userEntity=modelMapper.map(userBean, UserEntity.class);
		ContactEntity contactEntity=modelMapper.map(contactBean, ContactEntity.class);
		contactEntity.setUser(userEntity);
		
		ContactEntity result = contactDao.save(contactEntity);
		
		return result.getContact_Id();
	}
	
	public UserBean getUserByUsername(String email) {
		Optional<UserEntity> userEntity=userDao.getUserByUserName(email);
		UserBean result=null;
		if(userEntity.isPresent()) {
			result=modelMapper.map(userEntity.get(), UserBean.class);
		}
		return result;
	}
	
	public Page<ContactBean> findContactsByUserId(int userId, int pages, int pageSize){
		Pageable pageable=PageRequest.of(pages, pageSize, Sort.by("name"));
		
		Page<ContactEntity> contactList=contactDao.findContactsByUserId(userId, pageable);
		
		Page<ContactBean> resultList = contactList.map(objectEntity -> modelMapper.map(objectEntity, ContactBean.class));
		
		return resultList;
	}
	
	public ContactBean getContactByContactId(int contact_id) {
		Optional<ContactEntity> contactOptional=contactDao.findById(contact_id);
		ContactEntity contact=contactOptional.get();
		ContactBean result=modelMapper.map(contact, ContactBean.class);
		return result;
	}
	
	public int deleteContact(ContactBean contactBean) throws Exception {
		Resource filePath= resourceLoader.getResource("classpath:static/img/"+contactBean.getImage());
		if(filePath.exists() && !contactBean.getImage().equals("default.png")) {
			filePath.getFile().delete();
		}
		contactDao.deleteById(contactBean.getContact_Id());
		return contactBean.getContact_Id();
	}

	public int updateContact(ContactBean contactBean, MultipartFile image) throws Exception {
		if(!image.isEmpty()) {
			Resource filePath= resourceLoader.getResource("classpath:static/img/"+contactBean.getImage());
			if(filePath.exists() && !image.getOriginalFilename().equals("default.png")) {
				filePath.getFile().delete();
			}
			Path path=getPath(image);
			contactBean.setImage(path.toString().substring(path.toString().lastIndexOf("\\") + 1));
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		
		ContactEntity contactEntity=modelMapper.map(contactBean, ContactEntity.class);
		contactDao.save(contactEntity);
		return contactEntity.getContact_Id();
	}
	
	public List<ContactBean> searchContact(String query, UserBean userBean){
		 List<ContactEntity> resultEntity = contactDao.findByColumn(query, userBean.getId());
		 List<ContactBean> resultBean = new ArrayList<ContactBean>();
		 for(ContactEntity c: resultEntity) {
			 resultBean.add(modelMapper.map(c,ContactBean.class));
		 }
		 return resultBean;
	}
	
	public boolean changePassword(UserBean userBean, String oldPassword, String newPassword) throws Exception {
		if(passwordEncoder.matches(oldPassword, userBean.getPassword())) {
			UserEntity userEntity=modelMapper.map(userBean, UserEntity.class);
			userEntity.setPassword(passwordEncoder.encode(newPassword));
			userDao.save(userEntity);
			return true;
		}
		return false;
	}
	
	public void resetPassword(UserBean userBean, String newPassword) throws Exception {
		UserEntity userEntity=modelMapper.map(userBean, UserEntity.class);
		userEntity.setPassword(passwordEncoder.encode(newPassword));
		userDao.save(userEntity);
	}

}
