package com.scm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	// this is responsible to send email..
	
	@Autowired
    private JavaMailSender emailSender;
	
	public void sendEmail(String text, String subject, String to) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("noreply@baeldung.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        try {
        	emailSender.send(message);
        	System.out.println("Sent Successfully.....");
        }
        catch(Exception e) {
        	throw e;
        }
	}

}
