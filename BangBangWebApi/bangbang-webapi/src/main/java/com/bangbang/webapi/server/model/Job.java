package com.bangbang.webapi.server.model;

import java.util.Date;



/**
 * Created by wisp on 3/29/14.
 * DAO
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

}
