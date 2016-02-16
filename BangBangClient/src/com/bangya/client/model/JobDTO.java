package com.bangya.client.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.bangya.client.Util.Constants;
import com.joeapp.bangya.R;



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
    
	public int getJobId()
	{
		return this.jobId;
	}
	public void setJobId(int jobId)
	{
		this.jobId = jobId;
	}
	public int getOwnerUid()
	{
		return this.ownerId;
	}
	public void setOwnerUid(int ownerUid)
	{
		this.ownerId = ownerUid;
	}
	public int getJobStatusInt()
	{
		return this.status.getValue();
	}
	public JobStatus getJobStatus()
	{
		return this.status;
	}
	public String getJobStatusStr(Context context,JobStatus jobStatus)
	{
		if(jobStatus.equals(JobStatus.PUBLISHED))	{
			return context.getString(R.string.jobstatus_published).toString();	
		}	
		if(jobStatus.equals(JobStatus.PICKED)){
			return context.getString(R.string.jobstatus_accepted).toString();
		}
		if(jobStatus.equals(JobStatus.COMPLETED)){
			return context.getString(R.string.jobstatus_completed).toString();	
		}
		if(jobStatus.equals(JobStatus.CLOSED)){
			return context.getString(R.string.jobstatus_closed).toString();	
		}
		if(jobStatus.equals(JobStatus.EXPIRED)){
			return context.getString(R.string.jobstatus_expired).toString();	
		}
		Log.i("bangbangjob","invalied input : "+jobStatus);
		return "INVALIDE";
	}
	public void setJobStatus(JobStatus jobStatus)
	{
		this.status = jobStatus;
	}
	public int getPickerUid()
	{
		return this.pickerId;
	}
	public void setAcceptedUid(int accptedUid)
	{
		this.pickerId = accptedUid;
	}
	public String getTitle()
	{
		return this.title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getRewardTypeFromIntToString(Context context,int rewardType)
	{	
		switch(rewardType)
		{
			case Constants.REWARD_TYPE_RMB:
			return context.getString(R.string.job_reward_type_rmb).toString();	
			
			case Constants.REWARD_TYPE_OBJECT:
			return context.getString(R.string.job_reward_type_object).toString();	
			
			case Constants.REWARD_TYPE_ZAN:
			return context.getString(R.string.job_reward_type_zan).toString();	
			
			case Constants.REWARD_TYPE_OTHER:
			return context.getString(R.string.job_reward_type_other).toString();	
			
			case Constants.REWARD_TYPE_NULL:
			return context.getString(R.string.job_reward_type_null).toString();	
			
			default:
			Log.i("bangbangjob","invalied input : "+rewardType);
		}
		return Constants.INVALIDE_STRING;
	}
	public int getRewardType()
	{
		if(rewardType == null){
			return this.rewardType.NOREWARD.getValue();
		}
		return this.rewardType.getValue();
	}
	public void setRewardType(RewardType rewardType)
	{
		this.rewardType = rewardType;
	}

	public String getReward()
	{
		return this.reward;
	}
	public void setReward(String reward)
	{
		this.reward = reward;
	}

	public double getLongitude()
	{
		return this.longitude;
	}
	public void setLocationLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	public double getLatitude()
	{
		return this.latitude;
	}
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	public Date getDuetime()
	{
		return this.dueTime;
	}
	public void setDuetime(Date dutTime)
	{
		this.dueTime = dutTime;
	}
	public String getDuetimeAsString()
	{
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(this.getDuetime());
	}
	public String getOwnerNickName()
	{
		return this.ownerNickName;
	}
	public String getOwnerGender()
	{
		return this.ownerGender;
	}
	public int getOwnerPoints()
	{
		return this.ownerPoints;
	}
	public String getOwnerheadPhoto()
	{
		return this.ownerImage;
	}
	public String getPickerNickName()
	{
		return this.pickerNickName;
	}
	public String getPickerGender()
	{
		return this.pickerGender;
	}
	public int getPickerPoints()
	{
		return this.pickerPoints;
	} 
	public String getPickerheadPhoto()
	{
		return this.pickerImage;
	}
	public int getPointToOwner()
	{
		return this.pointsToOwner;
	}
	public int getPointToPicker()
	{
		return this.pointsToPicker;
	}
	public double getDistance()
	{
		return this.distance;
	}
	public Date getOwnerHeadPhotoMTime()
	{
		return this.owner_head_photo_modify_at;
	}
	public Date getPikcerHeadPhotoMTime()
	{
		return this.picker_head_photo_modify_at;
	}
	public String getDistanceForShow()
	{
		int iDistance = (int)(this.distance * 1000);
		if(iDistance >= 1000){
			return (Integer.toString(iDistance/1000)+" 千米");
		}else
		{
			return (Integer.toString(iDistance)+" 米");

		}
	}
	public int getOwnerCompleteJobCount()
	{
		return this.ownerCompleteCount;
	}
	public int getPickerCompleteJobCount()
	{
		return this.pickerCompleteCount;
	}
	public Date getOwnerBirthDay(){
		return this.owner_birthDay;
	}
	public int getOwnerAge(){
		return this.owner_age;
	}
	public Date getPickerBirthDay(){
		return this.picker_birthDay;
	}
	public int getPickerAge(){
		return this.picker_age;
	}
	 
}
