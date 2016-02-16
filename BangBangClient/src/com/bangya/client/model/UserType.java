package com.bangya.client.model;

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
    public int getValue(){
    	return value;
    }
}
