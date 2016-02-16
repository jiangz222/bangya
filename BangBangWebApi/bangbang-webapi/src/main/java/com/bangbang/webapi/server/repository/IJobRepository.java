package com.bangbang.webapi.server.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.bangbang.webapi.server.model.*;

public interface IJobRepository {
    List<Job> getJobByOwnerId(int ownerId);
    Job getJobById(int Id);
    boolean createJob(int ownerId, String title, String desc, Date dueTime,
                  double longitude, double latitude,
                  RewardType rewardType, String reward);
    ArrayList<JobDTO> getPulishedJob(int owneruid,int reqPage, JobStatus jobStatus);
    ArrayList<JobDTO> getPickedJob(int pickeruid,int reqPage, JobStatus jobStatus);
    ArrayList<JobDTO> getJobByLocation(int reqUid,int reqJobNumbers,JobStatus jobStatus,double longitude,double latitude,int range,String gender);
	boolean updateJobStatus(int jobid,int point,int pointDirection,int pickerUid,JobStatus fromJobStatus,JobStatus toJobStatus);

    //List<Job> searchJob (List<Condition> conditions);

    boolean deleteJob(int id);
}

