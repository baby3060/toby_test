package com.tobexam.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Dummy(가짜) 메일 Sender
 */
public class DummyMailSender implements MailSender {
    private String host;

    public void setHost( String host) {
        this.host = host; 
    }
    public void send(SimpleMailMessage mailMessage) throws MailException {
        System.out.println("가짜 메일 전송");
    }
    public void send(SimpleMailMessage[] mailMessageArr) throws MailException {}
}