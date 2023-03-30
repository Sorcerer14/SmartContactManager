package com.scm.business.bean;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ContactBean {
	private int contact_Id;
	@NotBlank(message="Contact name must not be Empty !!")
	private String name;
	private String secondName;
	private String work;
	@Email(regexp="^$|^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message="Must be well formed email address")
	private String email;
	@NotBlank(message="Phone Number must not be Empty !!")
	private String phone;
	private String image;
	private String description;
	@JsonIgnore
	private UserBean user;
	
	public ContactBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public int getContact_Id() {
		return contact_Id;
	}
	public void setContact_Id(int contact_Id) {
		this.contact_Id = contact_Id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UserBean getUser() {
		return user;
	}
	public void setUser(UserBean user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return "ContactBean [contact_Id=" + contact_Id + ", name=" + name + ", secondName=" + secondName + ", work="
				+ work + ", email=" + email + ", phone=" + phone + ", image=" + image + ", description=" + description
				+ "]";
	}
}
