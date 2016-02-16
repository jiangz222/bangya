package com.bangbang.webapi.server.repository;

import com.bangbang.webapi.server.model.BangYaPushType;
import com.bangbang.webapi.server.model.Job;
import com.bangbang.webapi.server.model.JobDTO;
import com.bangbang.webapi.server.model.JobStatus;
import com.bangbang.webapi.server.model.MessagesDTO;
import com.bangbang.webapi.server.model.RewardType;
import com.bangbang.webapi.server.model.User;
import com.bangbang.webapi.server.pushmanager.IPushManager;
import com.bangbang.webapi.server.pushmanager.PushManager;
import com.bangbang.webapi.server.util.BYConstants;
import com.bangbang.webapi.server.util.BYConstants;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class JobRepository implements IJobRepository {
	 	DBRepositoryOperator dbOp = new DBRepositoryOperator();

    /* (non-Javadoc)
     * @see com.bangbang.webapi.server.repository.IJobRepository#getJobByOwnerId(int)
     */
    @Override
    public List<Job> getJobByOwnerId(int ownerId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.bangbang.webapi.server.repository.IJobRepository#getJobById(int)
     */
    @Override
    public Job getJobById(int Id) {
        // TODO Auto-generated method stub
        return new Job();
    }

    /* (non-Javadoc)
     * @see com.bangbang.webapi.server.repository.IJobRepository#CreateJob(int, java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean createJob(int ownerId, String title, String desc, Date dueTime, double longitude, double latitude,
                         RewardType rewardType, String reward) {
        // TODO Auto-generated method stub
   	 	Object newJid = null;
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("title", title);
    	map.put("description", desc);
    	map.put("ownerId", ownerId);
    	map.put("dueTime", dueTime);
    	map.put("longitude", longitude);
    	map.put("latitude", latitude);
    	map.put("rewardType", rewardType.getValue());
    	map.put("reward", reward);
    	map.put("publishTime", new Date());
    	map.put("status", JobStatus.PUBLISHED.getValue());
    	map.put("modifyTime", new Date());

		try {
			newJid = (long)dbOp.insertOnelineDB(map, "Job");
			//no need to return job for create
			//job = (Job)dbOp.queryOneObjectbySingleCondition("jobId",newJid,"Job",job);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null == newJid)
		{
			return false;
		}else
		{//update publish counter of User
			if(dbOp !=null)
			{
				try {
					dbOp.updatePublishCounter(ownerId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//ignore the counter updating failed
				}

			}
			return true;
		}		
    }

    /* (non-Javadoc)
     * @see com.bangbang.webapi.server.repository.IJobRepository#DeleteJob(int)
     */
    @Override
    public boolean deleteJob(int id) {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public ArrayList<JobDTO> getPulishedJob(int owneruid, int reqPage, JobStatus jobStatus) {
		// TODO Auto-generated method stub
		ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();
   	 	
		try {
			jobList = dbOp.queryPublishedJob(owneruid,reqPage,jobStatus);
			System.out.println("get published job:"+jobList.size()+owneruid+reqPage+jobStatus.getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jobList = null;
		}
    	return jobList;
	}

	@Override
	public ArrayList<JobDTO> getPickedJob(int pickeruid, int reqPage,
			JobStatus jobStatus) {
		// TODO Auto-generated method stub
		ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();
   	 	
		try {
			jobList = dbOp.queryPickedJob(pickeruid,reqPage,jobStatus);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jobList = null;
		}
    	return jobList;
	}

	@Override
	public ArrayList<JobDTO> getJobByLocation(int reqUid,int reqJobNumbers,
			JobStatus jobStatus, double longitude, double latitude, int range,
			String gender) {
		// TODO Auto-generated method stub
		ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();

		try {
			jobList = dbOp.queryJobByLocation(reqUid,reqJobNumbers,jobStatus.getValue(),longitude,latitude,range,gender);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jobList = null;
		}
    	return jobList;
	}

	public boolean updateJobStatus(int jobid,int point,int pointDirection,int pickerUid,JobStatus fromJobStatus,JobStatus toJobStatus)
	{
		// TODO Auto-generated method stub
		int pointsToOwner = 0;
		int pointsToPicker = 0;
		int pushToUid = 0;
		int pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_NULL; 
		Job job = new Job();
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("jobId", jobid);

		try {
			//check if the job of specify jobstatus is exist?
			job = (Job)dbOp.queryObjectByKeyid(jobid,job,"Job","jobId");
			if(null == job || job.status.getValue() != fromJobStatus.getValue())
			{
				System.out.println("query job by jobid and stauts failed:"+jobid+fromJobStatus.getValue());
				return false;
			}
			if(JobStatus.PUBLISHED.equals(fromJobStatus) && JobStatus.PICKED.equals(toJobStatus))
			{//pub -> pick 
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	map.put("pickTime", new Date());
		    	map.put("pickerId", pickerUid);
		    	pushToUid = job.ownerId;
		    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 
			}else
			if(JobStatus.PICKED.equals(fromJobStatus) && JobStatus.COMPLETED.equals(toJobStatus)){
				//pick -> complete
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	map.put("completeTime", new Date());
		    	//start CompleteToCLose-Timer
		    	pushToUid = job.ownerId;
		    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 

			}else
			if(point != 0 && JobStatus.COMPLETED.equals(fromJobStatus) && JobStatus.COMPLETED.equals(toJobStatus)){
		    	//point
				//status not changed before bilateral points over
		    	map.put("modifyTime", new Date());
		    	System.out.println("complete 2 complete,"+"direction:"+pointDirection+"points:"+point);
		    	if(BYConstants.POINT_TO_PICKER == pointDirection){
			    	map.put("pointsToPicker", point);
			    	if(job.pointsToOwner != 0)
			    	{//bilateral points over,so job go to close
						map.put("status", JobStatus.CLOSED.getValue());
			    	}
			    	pushToUid = job.pickerId;
			    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_PICKER; 

		    	}else
		    	if(BYConstants.POINT_TO_OWNER == pointDirection){
			    	map.put("pointsToOwner", point);
			    	if(job.pointsToPicker != 0)
			    	{//bilateral points over,so job go to close
						map.put("status", JobStatus.CLOSED.getValue());
			    	}
			    	System.out.println("point to owner now");
			    	pushToUid = job.ownerId;
			    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 

		    	}
		    	//only need  to update Job points ,User Points update when complete to close
			
			}else
			if(JobStatus.COMPLETED.equals(fromJobStatus) && JobStatus.CLOSED.equals(toJobStatus)){
				//complete to close
		    	//completeToCLose-Timer expire ,update points if points is not 0
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	map.put("closeTime", new Date());
		    	//if points if invalid, set default value
		    	Integer tmpInterger = (Integer)dbOp.queryColumnValueByName(jobid,"jobId","pointsToOwner","Job");
				if(tmpInterger == null)
				{
					System.out.println("fail when request from Job pointsToOwner by:"+jobid);
					return false;
				}
		    	if(0 == tmpInterger.intValue()){
			    	map.put("pointsToOwner", 5);
			    	pointsToOwner = 5;
				}else
				{
					pointsToOwner = tmpInterger.intValue();
				}
		    	tmpInterger = (Integer) dbOp.queryColumnValueByName(jobid,"jobId","pointsToPicker","Job");
				if(tmpInterger == null)
				{
					System.out.println("fail when request from Job pointsToPicker by:"+jobid);
					return false;
				}
		    	if(0 == tmpInterger.intValue()){
			    	map.put("pointsToPicker", 5);
			    	pointsToPicker=5;
				}else
				{
					pointsToPicker = tmpInterger.intValue();
				}
		    	//complete to close by timer,notify both user of job,picker will be notified at the end of function
		    	pushToUid = job.ownerId;
		    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 

			}else
			if(JobStatus.PUBLISHED.equals(fromJobStatus) && JobStatus.EXPIRED.equals(toJobStatus))
			{//PUBLISHED-TIMER EXPIRED
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	pushToUid = job.ownerId;
		    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 


			}else
			if(JobStatus.PICKED.equals(fromJobStatus) && JobStatus.PUBLISHED.equals(toJobStatus)){
				//PICK TO PUBLISH, PICKER GIVE UP
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	map.put("pickerId", 1);// administrator as default,otherwise getJob fail 
		    	pushToUid = job.ownerId;
		    	pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER; 


			}else
			if(JobStatus.PUBLISHED.equals(fromJobStatus) && JobStatus.CLOSED.equals(toJobStatus)){
				//publish->close, publisher close this job 
		    	map.put("status", toJobStatus.getValue());
		    	map.put("modifyTime", new Date());
		    	map.put("closeTime", new Date());
			}
			int updateRowCount = 0;//useless here
		
			if(true == dbOp.updateOneRowDB(map, "Job", "jobId",updateRowCount))
			{//if complete to close,need update user's points and completeCount
				if(JobStatus.COMPLETED.equals(fromJobStatus) && JobStatus.CLOSED.equals(toJobStatus)){
			    	System.out.println("complete 2 close,update user info");

					if(false == dbOp.updateUserCompleteCounterAndPoints(job.ownerId, pointsToOwner)){
						System.out.println("updateCompleteCounterAndPoints failed,ownerId and points:"+job.ownerId+pointsToOwner);
					}
					if(false == dbOp.updateUserCompleteCounterAndPoints(job.pickerId, pointsToPicker)){
						System.out.println("updateCompleteCounterAndPoints failed,pickerId and points:"+job.pickerId+pointsToPicker);
					}
				}
				else if(point != 0 && JobStatus.COMPLETED.equals(fromJobStatus) && JobStatus.COMPLETED.equals(toJobStatus))
				{//if complete to complte ,and bilateral points over,so job actually goes to close,update user points and complete count
			    	System.out.println("complete 2 complete,update user info");

					if(BYConstants.POINT_TO_PICKER == pointDirection && job.pointsToOwner != 0){
			    		pointsToOwner = job.pointsToOwner ;
			    		pointsToPicker = point;
				    	if(false == dbOp.updateUserCompleteCounterAndPoints(job.pickerId, pointsToPicker)){
								System.out.println("updateCompleteCounterAndPoints failed,pickerId and points:"+job.pickerId+pointsToPicker);
							}
			    		 if(false == dbOp.updateUserCompleteCounterAndPoints(job.ownerId, pointsToOwner)){
							System.out.println("updateCompleteCounterAndPoints failed,ownerId and points:"+job.ownerId+pointsToOwner);						
							}

			    	}else
			    	if(BYConstants.POINT_TO_OWNER == pointDirection && job.pointsToPicker != 0){
			    		 pointsToPicker= job.pointsToPicker ;
			    		 pointsToOwner = point;		
			    		 if(false == dbOp.updateUserCompleteCounterAndPoints(job.pickerId, pointsToPicker)){
								System.out.println("updateCompleteCounterAndPoints failed,pickerId and points:"+job.pickerId+pointsToPicker);
							}
			    		 if(false == dbOp.updateUserCompleteCounterAndPoints(job.ownerId, pointsToOwner)){
							System.out.println("updateCompleteCounterAndPoints failed,ownerId and points:"+job.ownerId+pointsToOwner);						
							}
			    	}else
			    	{// bilateral points not over
						System.out.println("point not over, stay in complete status: ");
			    	}

				}
				//push job change to client
				if(pushToUid != 0){
				    IPushManager pm = new PushManager();
					//get push clientid of target user
					String clientId = (String)dbOp.queryColumnValueByName(pushToUid, "uid", "cid", "uidcidmap");
		
					if(null == clientId)
					{
						System.out.println("get client id fail by uid:"+pushToUid);
					}else{
						System.out.println("push to job jobid:"+job.jobId);
						pm.pushTransmissionMsgToSingle(clientId,
						    pm.constructPushMessage(0/*never used*/,BangYaPushType.JOB_STATUS,job.jobId,pushDirection,null));
					}
					if(JobStatus.COMPLETED.equals(fromJobStatus) && JobStatus.CLOSED.equals(toJobStatus)){
						//if complete to close timer work ,need notify owner and picker
						pushToUid = job.pickerId;
						pushDirection = BYConstants.PUSH_MSG_DIRECTION_TO_PICKER;
						clientId = (String)dbOp.queryColumnValueByName(pushToUid, "uid", "cid", "uidcidmap");
						
						if(null == clientId)
						{
							System.out.println("get client id fail by uid:"+pushToUid);
						}else{
							pm.pushTransmissionMsgToSingle(clientId,
							    pm.constructPushMessage(0/*never used*/,BangYaPushType.JOB_STATUS,job.jobId,pushDirection,null));
						}
					}
				}

				return true;
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	

}
