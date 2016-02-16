package com.bangbang.webapi.server.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bangbang.webapi.server.auth.BangBangAuth;
import com.bangbang.webapi.server.model.JobDTO;
import com.bangbang.webapi.server.model.JobStatus;
import com.bangbang.webapi.server.model.MessagesDAO;
import com.bangbang.webapi.server.model.MessagesDTO;
import com.bangbang.webapi.server.model.SystemDAO;
import com.bangbang.webapi.server.model.SystemDTO;
import com.bangbang.webapi.server.repository.IJobRepository;
import com.bangbang.webapi.server.repository.IMsgsRepository;
import com.bangbang.webapi.server.repository.ISystemRepository;
import com.bangbang.webapi.server.repository.JobRepository;
import com.bangbang.webapi.server.repository.MsgsRepository;
import com.bangbang.webapi.server.repository.SystemRepository;

@Path("messages")
@BangBangAuth
public class MessagesController {
    private IMsgsRepository repo = new MsgsRepository();

    @GET
    @Path("/byjobid")
    public  List<MessagesDTO> getMeassagesByJobId(@QueryParam("jobid") int jobid)
    {
    	if(jobid < 1)
    	{
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Cannot found the messages.").build());
    	}
    	List<MessagesDTO> jobList = new ArrayList<MessagesDTO>();
    	jobList = repo.GetMsgsByJobId(jobid);
    	if(jobList == null)
    	{
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Cannot found the messages.").build());
    	}else
    	{
    		return jobList;
    	}
    }
    @POST
    @Path("/insertOneMsg")
    public  MessagesDTO insertOneMsg(@QueryParam("jobid") int jobid,
    		@QueryParam("owneruid") int ownerUid,
    		@QueryParam("type") int type,
    		@QueryParam("msgContent") String msgContent) {
        System.out.println("into  insertOneMsg:"+ jobid+" "+ ownerUid+ " "+msgContent);

    	if(jobid<1 || ownerUid < 1 ||msgContent == null)
    	{
	       throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("insertOneMsg failed of bad parameters.").
	       			build());
    	}
    	MessagesDAO msg = repo.insertOneMsg(jobid,ownerUid,type,msgContent);
    	if(null == msg)
    	{
	       throw new WebApplicationException(Response.
	       			status(Response.Status.BAD_REQUEST).
	       			entity("updaet job failed.").
	       			build());
    	}
    	MessagesDTO msgDTO = MessagesDTO.ConvertFromDBObject(msg);
    	return msgDTO;
    }
}
