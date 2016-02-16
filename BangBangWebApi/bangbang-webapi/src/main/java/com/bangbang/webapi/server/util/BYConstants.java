package com.bangbang.webapi.server.util;

public class BYConstants {
	
	public static final String SEX_ALL="a";
	public static final String SEX_MALE ="m";
	public static final String SEX_FEMALE="f";
	
	public static final int POINT_TO_PICKER = 1;
	public static final int POINT_TO_OWNER = 2;
	public static final int POINT_BOTH = 3;

	public static final int DEFAULT_POINT = 5;
	
	public static final int PUSH_MSG_DIRECTION_TO_NULL = 0;
	public static final int PUSH_MSG_DIRECTION_TO_OWNER = 1;
	public static final int PUSH_MSG_DIRECTION_TO_PICKER = 2;
	
	public static final String EMAIL_WELCOME_TITLE= "欢迎加入帮呀！";
	public static final String EMAIL_WELCOME_CONTENT="您好! \n" +
															"    体会帮助快乐，发现身边美好! \n" +
															"    欢迎加入帮呀！ \n"+
															"    希望您在这能找寻到互相帮助所带来的最纯真欢乐\n"+
															"    学雷锋，不约炮！开始旅程吧！\n";
	public static final String EMAIL_RESET_PASSWORD_TITLE= "密码重置的验证码已送达！";

}
