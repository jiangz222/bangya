package com.bangbang.webapi.server.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import com.bangbang.webapi.server.auth.BangBangAuth;
import org.glassfish.grizzly.http.HttpContext;

import com.bangbang.webapi.server.model.*;
import com.bangbang.webapi.server.repository.*;

@Path("job")
@BangBangAuth
//@Produces(MediaType.APPLICATION_JSON)
public class JobController {
    private IJobRepository repo = new JobRepository();

    @GET
    @Path("/{id}")
    public JobDTO getJobById(@PathParam("id") int id)
    {
        System.out.println("into  getJobById:"+ id);

        Job job = repo.getJobById(id);
        if (job == null)
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Cannot found the job.").build());
        }
        JobDTO dto = new JobDTO();
        dto.jobId = job.jobId;
        dto.title = job.title;
        dto.description = job.description;
        dto.dueTime = job.dueTime;
        dto.longitude = job.longitude;
        dto.latitude = job.latitude;
        dto.rewardType = job.rewardType;
        dto.reward = job.reward;
        dto.publishTime = job.publishTime;
        dto.status = job.status;
        dto.pickTime = job.pickTime;
        dto.completeTime = job.completeTime;
        dto.closeTime = job.closeTime;
        dto.pointsToOwner = job.pointsToOwner;
        dto.pointsToPicker = job.pointsToPicker;

        IUserRepository userRepository = new UserRepository();
        User owner = userRepository.GetUserById(job.ownerId);
        if (owner != null)
        {
            dto.ownerId = owner.uid;
            dto.ownerNickName = owner.nickName;
            dto.ownerImage = owner.image;
            dto.ownerPoints = owner.points;
            dto.owner_head_photo_modify_at = owner.head_photo_modify_at;

        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Cannot found the owner.").build());
        }
        User picker = userRepository.GetUserById(job.ownerId);
        if (picker != null)
        {
            dto.pickerId = picker.uid;
            dto.pickerNickName = picker.nickName;
            dto.pickerImage = picker.image;
            dto.pickerPoints = picker.points;
            dto.picker_head_photo_modify_at = picker.head_photo_modify_at;

        }
        return dto;
    }

    @GET
    public List<Job> getJobByOwnerId()
    {
        return repo.getJobByOwnerId(1);
    }

    @POST
    @Path("/newjob")
    public Response createJob(@QueryParam("owneruid") int ownerUid,
    						 @QueryParam("title") String title,
                         @QueryParam("description") String desc,
                         @QueryParam("duetime") String dueTimeStr,
                         @QueryParam("longitude") double longitude,
                         @QueryParam("latitude") double latitude,
                         @QueryParam("rewardtype") String srewardType,
                         @DefaultValue("") @QueryParam("rewarddescription") String reward)
    {
        System.out.println("into  createJob:");

		Date dueTime = null;
		try {
			dueTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dueTimeStr); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
       	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("due date parse error.").build());
		}
		RewardType rewardType = RewardType.fromString(srewardType);

		
		if(false == repo.createJob(ownerUid, title, desc, dueTime, longitude, latitude, rewardType, reward)){
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("create job failed.").
	       			build());

		}else{
			return Response.ok().entity("sccess whatever").build();
		}
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPulishedJob")
    public  ArrayList<JobDTO> getPulishedJob(@QueryParam("owneruid") int owneruid,
    		@QueryParam("reqjobstatus") int reqJobStatus,
    		@QueryParam("reqpage") int reqPage) {
        System.out.println("into  getPulishedJob:");

    	JobStatus jobStatus = JobStatus.values()[reqJobStatus-1];
    	ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();
    	jobList = repo.getPulishedJob(owneruid,reqPage,jobStatus);
    	if(jobList == null)
    	{
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("get job failed.").
	       			build());
    	}
    	return jobList;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPickedJob")
    public  ArrayList<JobDTO> getPickedJob(@QueryParam("pickeduid") int pickeduid,
    		@QueryParam("reqjobstatus") int reqJobStatus,
    		@QueryParam("reqpage") int reqPage) {
        System.out.println("into  getPickedJob:");

    	ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();

    	if(reqJobStatus<1)
    	{
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("get job failed.").
	       			build());
    	}
    	JobStatus jobStatus = JobStatus.values()[reqJobStatus-1];
    	jobList = repo.getPickedJob(pickeduid,reqPage,jobStatus);
    	if(jobList == null)
    	{
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("get job failed.").
	       			build());
    	}
    	return jobList;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getJobbyLocation")
    public  ArrayList<JobDTO> getJobByLocation(@QueryParam("requid") int reqUid,
    		@QueryParam("reqjobnumbers") int reqJobNumbers,
    		@QueryParam("reqjobstatus") int reqjobstatus,
    		@QueryParam("longitude") double longitude,
    		@QueryParam("latitude") double latitude,
    		@QueryParam("range") int range,
    		@QueryParam("gender") String gender) {
        System.out.println("into  getJobByLocation:");

    	if(reqjobstatus<1)
    	{
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("get job failed.").
	       			build());
    	}
    	JobStatus jobStatus = JobStatus.values()[reqjobstatus-1];
    	ArrayList<JobDTO> jobList = new ArrayList<JobDTO>();
    	jobList = repo.getJobByLocation(reqUid,reqJobNumbers,jobStatus,longitude,latitude,range,gender);
    	
    	if(jobList == null)
    	{
	       	throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("get job failed.").
	       			build());
    	}
    	return jobList;
    }
    
    @POST
    @Path("/updateStatus")
    public  Response UpdateJobStatus(@QueryParam("jobid") int jobid,
    		@QueryParam("points") int points,
    		@QueryParam("pointDirection") int pointDirection,
    		@QueryParam("pickerUid") int pickerUid,
    		@QueryParam("fromJobStatus") int fromJobStatus,
    		@QueryParam("toJobStatus") int toJobStatus) {
        System.out.println("into  UpdateJobStatus:"+jobid+points+pointDirection+pickerUid+fromJobStatus+toJobStatus);

    	if(toJobStatus<1)
    	{
	       throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("updaet job failed.").
	       			build());
    	}
    	JobStatus utoJobStatus = JobStatus.values()[toJobStatus-1];
    	JobStatus ufromJobStatus = JobStatus.values()[fromJobStatus-1];
    	if(false == repo.updateJobStatus(jobid,points,pointDirection,pickerUid,ufromJobStatus,utoJobStatus))
    	{
	       throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("updaet job failed.").
	       			build());
    	}
    	return Response.ok().entity("success whatever").build();
    }
    @POST  
    @Path("/updateHeadImage")     
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public  String updateHeadImage(@Context HttpServletRequest request) {

    		System.out.println("request here :"+request);
    	return "111";
    }

}

