package com.bangbang.webapi.server.controller;

import com.bangbang.webapi.server.auth.BangBangAuth;
import com.bangbang.webapi.server.model.ProfileDTO;
import com.bangbang.webapi.server.repository.IUserRepository;
import com.bangbang.webapi.server.repository.UserRepository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by wisp on 3/30/14.
 */
@Path("profile")
@BangBangAuth
@Produces(MediaType.APPLICATION_JSON)
public class ProfileController {

    IUserRepository repository = new UserRepository();

    @GET
    public ProfileDTO GetMyProfile(@Context SecurityContext context)
    {
        int userId = Integer.parseInt(context.getUserPrincipal().getName());
        return ProfileDTO.ConvertFromDBObject(repository.GetUserById(userId));
    }
}
