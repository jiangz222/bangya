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

import com.bangbang.webapi.server.model.SystemDAO;
import com.bangbang.webapi.server.model.SystemDTO;
import com.bangbang.webapi.server.repository.ISystemRepository;
import com.bangbang.webapi.server.repository.SystemRepository;

@Path("system")
@BangBangAuth
public class SystemController {
    private ISystemRepository repo = new SystemRepository();

    @GET
    @Path("/get")
    public SystemDTO getSystemInfo()
    {
    	System.out.println("try to get system info");
    	SystemDTO systemDTO = new SystemDTO();
    	SystemDAO systemDAO = repo.getSystemInfo();
    	if(null == systemDAO){
    		throw new WebApplicationException(Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Sign up failed for email exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
    	}
    	systemDTO = 	SystemDTO.converFromDAO(systemDAO);
    	return systemDTO;
    	
    }
}
