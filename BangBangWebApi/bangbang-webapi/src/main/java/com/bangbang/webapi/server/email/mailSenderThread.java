package com.bangbang.webapi.server.email;

import javax.mail.MessagingException;

public class mailSenderThread implements Runnable {

	String content = null;
	String title = null;
	String emailAddr = null;
	public mailSenderThread(String emailAddr,String title,String content){
		this.content = content;
		this.title = title;
		this.emailAddr = emailAddr;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(content == null || title == null || emailAddr == null){
			System.out.println("exit mailSenderThread with exception,content: "+content
					+"title:"+title+"emailAddr:"+emailAddr);
			return;
		}
    	MailSender msm= MailSenderFactory.getSender();
    	try {
			//msm.send(emailAddr, title, content);
			msm.send(emailAddr, title, content);

			System.out.println("send email to"+emailAddr);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
