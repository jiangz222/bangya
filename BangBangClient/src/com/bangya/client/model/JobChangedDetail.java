package com.bangya.client.model;

public class JobChangedDetail {

	private boolean isMsgRecevied;
	private boolean isJobStatusRefreshed;
	private int jobId;         //unique
	private int direction;//mark every push's direction in client
	
	public boolean IsMsgRecvd(){
		return this.isMsgRecevied;
	}
	public boolean isJobStatusRefreshed(){
		return this.isJobStatusRefreshed;
	}
	public int getJobId(){
		return this.jobId;
	}
	public void setJobId(int jobId){
		this.jobId = jobId;
	}
	public void updateMsgReced(boolean msgStatus){
		this.isMsgRecevied = msgStatus;
	}
	public void updateJobStatus(boolean isJobStatusChanged){
		this.isJobStatusRefreshed = isJobStatusChanged;
	}
	public void setDirection(int direction){
		//to specify client, one job can only has one direction!!
		this.direction = direction;
	}
	public int getDirection(){
		//to specify client, one job can only has one direction!!
		return this.direction;
	}
	
}