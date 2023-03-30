package com.scm.controller;

import java.security.Principal;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.scm.business.bean.ContactBean;
import com.scm.business.bean.UserBean;
import com.scm.exceptionHandler.Message;
import com.scm.service.ScmService;

@Controller
@RequestMapping("/user")
public class UserController /*implements ErrorController*/ {
	
	@Autowired
	private ScmService service;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName=principal.getName();
		UserBean data=service.getUserByUsername(userName);
		model.addAttribute("user", data);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "User Dashboard");
		return "regular/user_dashboard";
	}
	
	@GetMapping("/user-update")
	public String userUpdateForm(Model model) {
		model.addAttribute("title", "Update User");
		return "regular/update_user.html";
		
	}
	
	//Updating user
	@PostMapping("/process-user-update")
	public String processUserUpdate(@Valid @ModelAttribute("user") UserBean userBean, BindingResult result, Model model, HttpSession session, @RequestParam("profileImage") MultipartFile file) {
		if(result.hasErrors()) {
			System.out.println("Error "+result.toString());
			model.addAttribute("title", "Update User");
			return "regular/update_user.html";
		}
		
		try {
			int id=service.updateUser(userBean, file);
			session.setAttribute("message", new Message("User Updated!!!", "alert-success"));
			System.out.println("User updated with id "+id);
			
		} catch (Exception e) {
			session.setAttribute("message", new Message("Please Try Again!!", "alert-danger"));
			return "redirect:/user/user-update";
		}
		finally {
			System.gc();
		}
		
		return "redirect:/user/profile";
	}
	
	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new ContactBean());
		return "regular/add_contact_form.html";
	}
	
	// adding contact
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") ContactBean contact, BindingResult result, @RequestParam("profileImage") MultipartFile file, Principal principal, Model model, HttpSession session){
		//String filenameString= StringUtils.cleanPath(contact.getImage().getOriginalFilename());
		//System.out.println(filenameString);
		if(result.hasErrors()) {
			//System.out.println("Error "+result.toString());
			model.addAttribute("title", "Add Contact");
			model.addAttribute("contact", contact);
			return "regular/add_contact_form.html";
		}
		
		int id=0;
		
		try {
			String userName=principal.getName();
			UserBean user=service.getUserByUsername(userName);
			contact.setUser(user);
			id=service.save(contact,file);
			session.setAttribute("message", new Message("Contact Added", "alert-success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Please Try Again!!", "alert-danger"));
			return "redirect:/user/add-contact";
		}
		finally {
			System.gc();
		}
			
		return "redirect:/user/"+id+"/contact-details";
	}
	
	// Show contacts
	// per page=5
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		String username=principal.getName();
		UserBean user=service.getUserByUsername(username);
		Page<ContactBean> contactList = service.findContactsByUserId(user.getId(),page,5);
		model.addAttribute("contactList", contactList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactList.getTotalPages());
		return "regular/show_contacts.html";
	}
	
	//Showing Particular Contact Details
	@GetMapping("{contact_id}/contact-details")
	public String showContactDetail(@PathVariable("contact_id") Integer contactId, Model model, Principal principal){
		ContactBean contact=service.getContactByContactId(contactId); 
		String username=principal.getName();
		UserBean user=service.getUserByUsername(username);
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "regular/contact_detail.html";
	}
	
	@GetMapping("/delete/{contact_id}")
	public String deleteContact(@PathVariable("contact_id") Integer contactId, Principal principal, HttpSession session) {
		ContactBean contact=service.getContactByContactId(contactId); 
		String username=principal.getName();
		UserBean user=service.getUserByUsername(username);
		
		try {
			if(user.getId()==contact.getUser().getId()) {
				int id=service.deleteContact(contact);
				session.setAttribute("message", new Message("Contact Deleted Successfully....", "alert-success"));
				System.out.println(id+" Contact Id deleted");
			}
		}catch(Exception e) {
			session.setAttribute("message", new Message("Contact Cannot Be Deleted, Please Try Again!!", "alert-success"));
			return "redirect:/user"+contactId+"/contact-details";
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	//Update Form User Contact
	@PostMapping("/update/{contact_id}")
	public String updateForm(@PathVariable("contact_id") Integer contactId, Model model) {
		ContactBean contact=service.getContactByContactId(contactId); 
		model.addAttribute("title", "Update Contact");
		model.addAttribute("contact", contact);
		return "regular/update_contact.html";
	}
	
	//Updating User Contact
	@PostMapping("/process-update")
	public String updateContact(@Valid @ModelAttribute("contact") ContactBean contact, BindingResult result, @RequestParam("profileImage") MultipartFile file, Model model, Principal principal, HttpSession session) throws Exception {
		if(result.hasErrors()) {
			//System.out.println("Error "+result.toString());
			model.addAttribute("title", "Update Contact");
			model.addAttribute("contact", contact);
			return "regular/update_contact.html";
		}
		
		try {
			String username=principal.getName();
			UserBean user=service.getUserByUsername(username);
			contact.setUser(user);
			service.updateContact(contact,file);
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Contact Updated!!!", "alert-success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Contact Cannot Be Updated, Please Try Again!!", "alert-success"));
		}
		finally {
			System.gc();
		}
		
		return "redirect:/user/"+contact.getContact_Id()+"/contact-details";
	}
	
	//profile page handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "User Profile");
		return "regular/user_profile.html";
	}
	
	//user settings
	@GetMapping("/settings")
	public String userSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "regular/user_settings.html";
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) throws Exception {
		String username=principal.getName();
		UserBean user=service.getUserByUsername(username);
		if(service.changePassword(user, oldPassword, newPassword)) {
			session.setAttribute("message", new Message("Password Updated", "alert-success"));
			return "redirect:/user/index";
		}
		else {
			session.setAttribute("message", new Message("Wrong Old Password Entered", "alert-warning"));
			return "redirect:/user/settings";
		}
	}
	
	/*@RequestMapping("/error404")
	public String globalizedExceptionHandler(Exception e, HttpSession session, Model model) {
		model.addAttribute("title", "Warning");
		session.setAttribute("message", new Message("Something went wrong, Please Don't Panic!!", "alert-danger"));
		return "regular/user_dashboard.html";
	}*/
	
}
