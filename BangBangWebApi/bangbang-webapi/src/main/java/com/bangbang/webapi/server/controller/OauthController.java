package com.bangbang.webapi.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@Path("oauth")
public class OauthController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String secretService(@Context HttpServletRequest request) {
//        try {
//            if(!OAuthSignature.verify(request, params, secrets))
//                return "false";
//        } catch (OAuthSignatureException ose) {
//            return "false";
//        }

        return "OK";
    }
}

