package com.bangbang.webapi.server.model;

/**
 * Created by wisp on 3/29/14.
 */
public enum UserType {
    BANGBANG(1),
    WEIBO(2),
    QQ(3),
    WEIXIN(4);
    private final int value;
    private UserType(int value){
    	this.value = value;
    }
  // private  UserType(String s){
 //   	this.value = 0;

 //   }
    public int getValue(){
    	return value;
    }
    public static UserType fromString(String value)
    {
    	switch(Integer.valueOf(value))
    	{
    		case 1:
    			return UserType.BANGBANG;
    		case 2:
    			return UserType.WEIBO;
    		case 3:
    			return UserType.QQ;
    		case 4:
    			return UserType.WEIXIN;
    		default:
    			return UserType.BANGBANG;

    	}
    }
}
