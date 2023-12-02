package com.scm.controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bastiaanjansen.otp.TOTPGenerator;
import com.scm.business.bean.UserBean;
import com.scm.exceptionHandler.Message;
import com.scm.service.EmailService;
import com.scm.service.ScmService;

@Controller
public class HomeController {
	
	@Autowired
	private ScmService service;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TOTPGenerator tOTPGenerator;
	
	
	@RequestMapping("/")
	public ModelAndView homePage() {
		ModelAndView model=new ModelAndView();
		model.addObject("title", "Home - Smart Contact Manager");
		model.setViewName("home.html");
		return model;
	}
	
	@RequestMapping("/signup")
	public ModelAndView signupPage() {
		ModelAndView model=new ModelAndView();
		model.addObject("title", "Register - Smart Contact Manager");
		model.addObject("user", new UserBean());
		model.setViewName("signup.html");
		return model;
	}
	
	@RequestMapping(value="/do_register", method= RequestMethod.POST)
	public ModelAndView userRegistration(@Valid @ModelAttribute("user") UserBean userBean, BindingResult result, HttpSession session, @RequestParam(name = "profileImage", required = true) MultipartFile file) {
		//System.out.println("Error "+result.toString());
		//System.out.println(userBean);
		if(result.hasErrors()) {
			System.out.println("Error "+result.toString());
			return new ModelAndView("signup.html", "user", userBean);
		}
		
		ModelAndView model=new ModelAndView();
		try {
			int id=service.saveUser(userBean, file);
			model.addObject("user", new UserBean());
			session.setAttribute("message", new Message("Registration Successful", "alert-success"));
			System.out.println("User saved with id "+id);
			
		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong !!", "alert-danger"));
		}
		finally {
			System.gc();
		}
		
		model.setViewName("redirect:/user/index");
		return model;
	}
	
	@GetMapping("/signin")
	public ModelAndView customLogin() {
		return new ModelAndView("login.html", "title", "Login - Smart Contact Manager");
	}
	
	@GetMapping("/forgot")
	public ModelAndView openEmailForm() {
		ModelAndView model=new ModelAndView();
		model.addObject("title", "Forgot Email");
		model.setViewName("forgot_email_form.html");
		return model;
	}
	
	@PostMapping("/send-otp")
	public ModelAndView sendOTP(@RequestParam("email") String email, HttpSession session) {
		
		ModelAndView model=new ModelAndView();
		model.addObject("title", "Forgot Email");
		model.addObject("email", email);
		
		String otp = tOTPGenerator.now();
		//System.out.println(otp);
		String subject = "OTP From SCM";
		String message = "<h1>"+otp+"</h1> <h3> is your OTP</h3>";
		String to = email;
		//tOTPGenerator.verify(otp);
		
		//Code to send
		try {
			emailService.sendEmail(message, subject, to);
			model.setViewName("verify_otp_form.html");
		} catch (Exception e) {
			session.setAttribute("message", new Message("OTP cannot be sent!!", "alert-danger"));
			model.setViewName("redirect:/forgot");
			e.printStackTrace();
		}

		
		return model;
	}
	
	@PostMapping("/verify-otp")
	public ModelAndView verifyOtp(@RequestParam("otp") String otp, @RequestParam("email") String email, HttpSession session) {
		ModelAndView model=new ModelAndView();
		model.addObject("title", "Forgot Email");
		if(tOTPGenerator.verify(otp)) {
			session.setAttribute("email",email);
			model.setViewName("change_password.html");
		}
		else {
			model.addObject("email", email);
			session.setAttribute("message", new Message("OTP is invalid!!", "alert-danger"));
			model.setViewName("verify_otp_form.html");
		}
		
		return model;
	}
	
	@PostMapping("/change-password")
	public ModelAndView changePassword(@RequestParam("newpassword") String newPassword, HttpSession session) throws Exception {
		ModelAndView model=new ModelAndView();
		String email=(String) session.getAttribute("email");
		UserBean user=service.getUserByUsername(email);
		
		if(user==null) {
			session.setAttribute("message", new Message("No such user Exist!!", "alert-warning"));
			model.setViewName("redirect:/forgot");
		}
		
		service.resetPassword(user, newPassword);
		model.setViewName("redirect:/signin");
		return model;
	}
}
