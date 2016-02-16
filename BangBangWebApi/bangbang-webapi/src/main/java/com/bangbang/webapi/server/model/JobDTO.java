package com.bangbang.webapi.server.model;

import java.util.Date;



public class JobDTO {
    public int jobId;
    public String title;
    public String description;
    public int ownerId; //
    public Date dueTime;
    public double longitude;
    public double latitude;
    public JobStatus status; //published, picked, completed, closed, expired
    public RewardType rewardType; //null-0, rmb-1, object-2, zan-3, others-4
    public String reward;

    public int pickerId;
    public int pointsToOwner;
    public int pointsToPicker;
    public Date publishTime;
    public Date pickTime;
    public Date completeTime;
    public Date closeTime;
    public Date modifyTime;

    // followed is from User Table
    public String ownerNickName;
    public String pickerNickName;
    public String ownerImage;
    public String pickerImage;
    public int ownerPoints;
    public int pickerPoints;
    public int ownerCompleteCount;
    public int pickerCompleteCount;
    public String ownerGender;
    public String pickerGender;
    public Date picker_head_photo_modify_at;
    public Date owner_head_photo_modify_at;
    public Date owner_birthDay;
    public Date picker_birthDay;
    public int owner_age;
    public int picker_age;
    //below is new create column
    public double distance;
    
    public static JobDTO ConvertFromDBObject(Job job, User owner,User picker)
    {//never used
    	JobDTO jobDto = new JobDTO();

    	jobDto.jobId = job.jobId;
    	jobDto.title = job.title;
    	jobDto.description = job.description;
    	jobDto.ownerId = job.ownerId; //
    	jobDto.dueTime = job.dueTime;
    	jobDto.longitude = job.longitude;
    	jobDto.latitude = job.latitude;
    	jobDto.status = job.status; 
    	jobDto.rewardType = job.rewardType; 
    	jobDto.reward = job.reward;
    	jobDto.pickerId = job.pickerId;
    	jobDto.pointsToOwner = job.pointsToOwner;
    	jobDto.pointsToPicker = job.pointsToPicker;
    	jobDto.publishTime = job.publishTime;
    	jobDto.pickTime = job.pickTime;
    	jobDto.completeTime = job.completeTime;
    	jobDto.closeTime = job.closeTime;
    	jobDto.modifyTime = job.modifyTime;

    	jobDto.ownerNickName = owner.nickName;
    	jobDto.pickerNickName = picker.nickName;
    	jobDto.ownerImage = owner.image;
    	jobDto.pickerImage = picker.image;
    	jobDto.ownerPoints = owner.points;
    	jobDto.pickerPoints = picker.points;

       return jobDto;
    }
}
