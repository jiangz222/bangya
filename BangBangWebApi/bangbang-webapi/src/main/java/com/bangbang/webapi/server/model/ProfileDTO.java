package com.bangbang.webapi.server.model;

import java.util.Date;


/**
 * Created by wisp on 3/20/14.
 */
public class ProfileDTO {
    public int uid;
    public UserType type; //bangbang-0, weibo-1, qq-2, weixin-3
    public String username;
    public String externalId;
    public String email;
    public String password;

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
    
    public static ProfileDTO ConvertFromDBObject(User user)
    {
        ProfileDTO profile = new ProfileDTO();
        profile.uid = user.uid;
        profile.type = user.type;
        profile.username = user.username;
        profile.password = user.password;
        profile.email = user.email;
        profile.externalId = user.externalId;
        profile.nickName = user.nickName;
        profile.province = user.province;
        profile.city = user.city;
        profile.description = user.description;
        profile.image = user.image;
        profile.gender = user.gender;
        profile.age = user.age;
        profile.birthDay = user.birthDay;
        profile.points = user.points;
        profile.jobCompleteCount = user.jobCompleteCount;
        profile.jobPublishCount = user.jobPublishCount;
        profile.create_at = user.create_at;
        profile.modify_at = user.modify_at;
        profile.qqId = user.qqId;
        profile.weiXinId = user.weiXinId;
        profile.school = user.school;
        profile.company = user.company;
        profile.head_photo_modify_at = user.head_photo_modify_at;
        return profile;
    }
}
