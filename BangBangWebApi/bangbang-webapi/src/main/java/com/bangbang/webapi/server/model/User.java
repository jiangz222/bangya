package com.bangbang.webapi.server.model;

import java.util.Date;


/**
 * @author wisp
 *
 */
public class User {
    public int uid;
    public UserType type; //bangbang-0, weibo-1, qq-2, weixin-3
    public String username;  //username in db should be null,logically can be email,phone number,SOCIAL name
    public String password;
    public String email;
    private String salt; // used to encrypt the password
    public String externalId;

    public String nickName;
    public int province;
    public int city;
    public String description;
    public String image;
    public String gender; //male-m, female-f, unknown-u,all-a
    public int age;
    public Date birthDay;
    public int points;
    public int jobCompleteCount;
    public int jobPublishCount;

    public Date create_at;
    public Date modify_at;

    public String qqId;
    public String weiXinId;
    public String school;
    public String company;
    public Date head_photo_modify_at;
    
    public String identifyCode;
    
    public User getInvalideUser()
    {
    	User user = new User();
    	
    	return user;
    }
}