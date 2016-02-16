package com.bangya.client.model;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bangya.client.BBUI.HomeActivity;
import com.bangya.client.BBUI.MyTrends;
import com.bangya.client.Util.Constants;
import com.bangya.client.comm.BangYaNotification;


public class PushChangeClientInfo {
	private boolean isChanged;  //if any new changed to job or msgs,used to set MYTRENDS in UI
	private int direction;      //null, picked ,published or both,used to set UI in TRENDS
	private LinkedList<JobChangedDetail> changedDetail;  //record 1.the changed is msgs 、status or both, and 2. changed jobid
	private String TAG = "PCCI";
	public PushChangeClientInfo(){
		this.isChanged = false;
		this.direction = Constants.PUSH_MSG_DIRECTION_TO_NULL;
		this.changedDetail = new LinkedList<JobChangedDetail>();
	}
	public boolean IsChanged(){
		return this.isChanged;
	}
	public LinkedList<JobChangedDetail> getChangdDetail(){
		return this.changedDetail;
	}

	public boolean isDirectionEnable(int iDirection){
		if(0 != (this.direction & iDirection)){
			return true;
		}
		return false;
	}
	public void setDirection(int iDirection){
		this.direction |= iDirection;
	}
	public void clearDirection(int iDirection){
		if(iDirection == Constants.PUSH_MSG_DIRECTION_TO_BID){
			this.direction = Constants.PUSH_MSG_DIRECTION_TO_NULL;
			return;
		}
		int tmp = this.direction & iDirection;
		if(tmp != 0)//tmp is 0 means this direction never enable
		this.direction ^= iDirection;
	}
	public void setChanged(boolean changed){
		this.isChanged = changed;
	}
	public boolean isJobStatusRefreshed(int reqJobId){
		for(JobChangedDetail cd : this.getChangdDetail()){
			if(cd.getJobId() == reqJobId)
			{
				if(cd.isJobStatusRefreshed()){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isMsgNotified(int reqJobId){
		for(JobChangedDetail cd : this.getChangdDetail()){
			if(cd.getJobId() == reqJobId)
			{
				if(cd.IsMsgRecvd()){
					return true;
				}
			}
		}
		return false;
	}

	public  void addClientInfo(PushMsgDTO pushMsg){
    	boolean findJobExisted = false;

    	this.setChanged(true);
		if(pushMsg.direction == Constants.PUSH_MSG_DIRECTION_TO_OWNER){
			this.setDirection(Constants.PUSH_MSG_DIRECTION_TO_OWNER);
		}else
		{
			this.setDirection(Constants.PUSH_MSG_DIRECTION_TO_PICKER);
		}
		//if this job is already mark as changed,upate exsit JobChangedDetail object
		for(JobChangedDetail cd : this.getChangdDetail()){
			if(pushMsg.jobId == cd.getJobId())
			{
				if(BangYaPushType.CHAT_MSG == pushMsg.pushType){
					
					cd.setDirection(pushMsg.direction);
					cd.updateMsgReced(true);
					
				}else{
					cd.updateJobStatus(true);
					cd.setDirection(pushMsg.direction);
				}
				findJobExisted = true;
				Log.i(TAG, "addClientInfo,jobId:"+cd.getJobId()+"direction:"+cd.getDirection()+"isMsg:"+cd.IsMsgRecvd()+"isStatus:"+cd.isJobStatusRefreshed());

				break;
			}

		}
		//if not, create new object
		if(!findJobExisted){
			JobChangedDetail cd = new JobChangedDetail();					
			if(BangYaPushType.CHAT_MSG == pushMsg.pushType){
				cd.updateMsgReced(true);
			}else{
				cd.updateJobStatus(true);
			}
			cd.setDirection(pushMsg.direction);
			cd.setJobId(pushMsg.jobId);
			this.getChangdDetail().addLast(cd);
			Log.i(TAG, "addClientInfo,jobId:"+cd.getJobId()+"direction:"+cd.getDirection()+"isMsg:"+cd.IsMsgRecvd()+"isStatus:"+cd.isJobStatusRefreshed());

		}

		HomeActivity homeAct = new HomeActivity();
		//update HomeActivity
		homeAct.setTabIcon(TabIdx.MY_TRENDS.getValue());

		return;
	}
	public void clearClientInfo(BangYaPushType msgType, int jobId,Context ct){
		Log.i(TAG, "clearClientInfo,jobId:"+jobId+"msgType:"+msgType);
		for(JobChangedDetail cd : this.getChangdDetail()){
			if(jobId == cd.getJobId())
			{
				//to specify job,no matter how many msgs or job-status pushed to client
				//clear  to zero if user click related UI
				if(BangYaPushType.CHAT_MSG == msgType){
					Log.i(TAG, "remove chat msg in changedDetail");
					cd.updateMsgReced(false);
					if(false == cd.isJobStatusRefreshed()){
						this.getChangdDetail().remove(cd);
					}
				}else{
					Log.i(TAG, "remove statuschanged in changedDetail");

					cd.updateJobStatus(false);
					if(false == cd.IsMsgRecvd()){
						this.getChangdDetail().remove(cd);
					}
				}
				Log.i(TAG, "after remove size of changedDetail: "+this.getChangdDetail().size());

				break;
			}
		}
		boolean toOwnerExist = false;
		boolean toPickerExist = false;
		for(JobChangedDetail cd : this.getChangdDetail()){
			// if other job need ui notify, published or picker in my Trends can't clear
			if(!toOwnerExist && cd.getDirection() == Constants.PUSH_MSG_DIRECTION_TO_OWNER){
				toOwnerExist = true;
			}
			if(!toPickerExist && cd.getDirection() == Constants.PUSH_MSG_DIRECTION_TO_PICKER){
				toPickerExist = true;
			}
		}

		if(this.getChangdDetail().size() == 0)
		{
			Log.i(TAG, "ALL CLEAR");
			this.clearDirection(Constants.PUSH_MSG_DIRECTION_TO_BID);
			this.setChanged(false);
			HomeActivity homeAct = new HomeActivity();
			homeAct.clearTabIcon(1);
    		// if all new push update are cleared, clear notification if it exists
    		BangYaNotification.clearNotifycation(ct);
		}else{
			Log.i(TAG, "clearClientInfo,toPickerExist:"+toPickerExist+"toOwnerExist:"+toOwnerExist);
			if(!toOwnerExist){
				this.clearDirection(Constants.PUSH_MSG_DIRECTION_TO_OWNER);
			}
			if(!toPickerExist){
				this.clearDirection( Constants.PUSH_MSG_DIRECTION_TO_PICKER);
			}

		}
		return;
	}
	
}
