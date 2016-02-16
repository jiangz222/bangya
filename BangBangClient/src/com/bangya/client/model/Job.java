package com.bangya.client.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.bangya.client.BBUI.Published;
import com.bangya.client.Util.Constants;
import com.joeapp.bangya.R;



/**
 * Created by wisp on 3/29/14.
 */
public class Job {
    public int jobId;
    public String title;
    public String description;
    public int ownerId; //
    public Date dueTime;
    public JobStatus status; //published, picked, completed, closed, expired
    public RewardType rewardType; //null-0, rmb-1, object-2, zan-3, others-4
    public String reward;
    public double longitude;
    public double latitude;
    public Date publishTime;
    public int pickerId;
    public Date pickTime;

    public int pointsToOwner;
    public int pointsToPicker;
    public Date completeTime;

    public Date closeTime;
    public Date modifyTime;


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
	public int getAcceptedUid()
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
		if(this.getDuetime() != null){
			return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(this.getDuetime());
		}else{
			return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());
		}
	}
}
