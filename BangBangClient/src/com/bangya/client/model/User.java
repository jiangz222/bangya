package com.bangya.client.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.Constants;


/**
 * @author wisp
 *
 */
public class User implements Cloneable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int uid;
    public UserType type; //bangbang-0, weibo-1, qq-2, weixin-3
    public String username;  // not used now,username can not be configured, can be email,cellphone number or name from weibo &weixin
    public String password;
    public String email;
    public String salt; // used to encrypt the password
    public String externalId;

    public String nickName;
    public int province;
    public int city;
    public String description;
    public String image;
    public String gender; //male-m, female-f, unknown-u
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

    public String identifyCode; // server never send meaningful value to client
    public int getUserId()
    {
    	return this.uid;
    }
    public void setUserId(int uid)
    {
    	this.uid = uid;
    }
   public String getUserName()
    {
    	return this.username;
    }
   
   public void setUserName(String username)
    {
    	this.username = username;
    }
   public String getNickName()
   {
   	return this.nickName;
   }
  public void setNickName(String nickName)
   {
   	this.nickName = nickName;
   }
   public String getPassWord()
   {
   	return this.password;
   }
   public void setPassWord(String password)
   {
	   this.password = password;
   }
   public String getEmail()
   {
	   return this.email;
   }
   public void setEmail(String email)
   {
	   this.email = email;
   }
   
   public String getImageHead()
   {
	   return this.image;
   }
   public void setImageHead(String image)
   {
	   this.image = image;
   }
   public int getAge()
   {
	   return this.age;
   }
   public void setAge(int age)
   {
	   this.age = age;
   }	
	public String getGenderName()
	{
		if(Constants.FEMALE.equals(this.gender))
		{
			return "女";
		}
		else if(Constants.MALE.equals(this.gender))
		{
			return "男";
		}
		else{
			return "无";
		}
	}	
	public void setGender(String gender)
	{
		this.gender = gender;
	}
	public String getGender()
	{
		return this.gender;
	}
	public String getCityName()
	{
		if(0 == this.city)
		{
			return "北京";
		}
		else
		{
			return "未完成";
		}
	}
	public int getCityCode()
	{
		return this.city;
	}
	public void setCityCode(int city)
	{
		this.city = city;
	}

	public int getPoints()
	{
		return this.points;
	}
	public void setPoints(int points)
	{
		this.points = points;
	}
	public Date getBirthDay()
	{
		return this.birthDay;
	}
	public void setBirthDay(Date birthDay)
	{
		this.birthDay = birthDay;
	}
	public static String getBirthDayAsStirng(Date birthDay)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(birthDay);
	}
	public int getCompleteJobCount()
	{
		return this.jobCompleteCount;
	}
	public void setCompleteJobCount(int jobCompleteCount){
		this.jobCompleteCount = jobCompleteCount;
	}
   public Date getHeadPhotoMTime(){
    	return head_photo_modify_at;
    }
   public void setHeadPhotoMTime(Date dt){
   		head_photo_modify_at =dt;
   }
	/**
	 * 实现对象直接 = 是赋值的功能
	 */
    public Object clone()    
    {    
        Object o=null;    
       try    
        {    
        o=(User)super.clone();    
        }    
       catch(CloneNotSupportedException e)    
        {    
            System.out.println(e.toString());    
        }    
       return o;    
    }   
}