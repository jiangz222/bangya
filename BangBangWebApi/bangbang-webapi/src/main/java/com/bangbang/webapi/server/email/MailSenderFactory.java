package com.bangbang.webapi.server.email;

public class MailSenderFactory {
	   /**
     * 服务邮箱
     * sender email box
     */
	private static  String senderMailBoxUserName = "service@bangya365.com";
	private static String senderMailBoxUserPwd = "by365robot";
    private static MailSender serviceSms = null;
    
	public static MailSender getSender()
	{
		if (serviceSms == null) {
			serviceSms = new MailSender(senderMailBoxUserName,
	        		   senderMailBoxUserPwd);
		}
		return serviceSms;
	}
}
