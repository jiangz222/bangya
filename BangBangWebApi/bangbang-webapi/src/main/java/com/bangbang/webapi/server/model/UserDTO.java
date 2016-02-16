package com.bangbang.webapi.server.model;

import java.util.Date;

// never used, use profileDTO

/**
 * Created by wisp on 3/29/14.
 */
public class UserDTO {
    public int uid;
    public UserType type; //bangbang-0, weibo-1, qq-2, weixin-3
    public String nickName;
    public String email;
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

    public String qqId;
    public String weiXinId;
    public String school;
    public String company;
    public Date head_photo_modify_at;
    
    public static UserDTO ConvertFromDbOjbect(User user)
    {
        UserDTO userDTO = new UserDTO();
        userDTO.uid = user.uid;
        userDTO.type = user.type;
        userDTO.nickName = user.nickName;
        userDTO.province = user.province;
        userDTO.city = user.city;
        userDTO.description = user.description;
        userDTO.image = user.image;
        userDTO.gender = user.gender;
        userDTO.age = user.age;
        userDTO.birthDay = user.birthDay;
        userDTO.points = user.points;
        userDTO.jobCompleteCount = user.jobCompleteCount;
        userDTO.jobPublishCount = user.jobPublishCount;
        userDTO.qqId = user.qqId;
        userDTO.weiXinId = user.weiXinId;
        userDTO.school = user.school;
        userDTO.company = user.company;
         userDTO.head_photo_modify_at = user.head_photo_modify_at;
        return userDTO;
    }
}
