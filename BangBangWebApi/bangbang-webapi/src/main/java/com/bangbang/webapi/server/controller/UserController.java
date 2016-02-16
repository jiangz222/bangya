package com.bangbang.webapi.server.controller;


import java.io.File;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Base64;
//import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import org.springframework.security.crypto.codec.Base64;

import com.bangbang.webapi.server.auth.BCrypt;
import com.bangbang.webapi.server.auth.BangBangAuth;
import com.bangbang.webapi.server.auth.MyRSA;
import com.bangbang.webapi.server.email.mailSenderThread;
import com.bangbang.webapi.server.model.*;
import com.bangbang.webapi.server.repository.*;
import com.bangbang.webapi.server.util.BYConstants;

@Path("/")
public class UserController {
	private String headImagePath = "/home/dev/bangya/image/head/";
   IUserRepository repo = new UserRepository();
	MyRSA rsa = new MyRSA();

    @POST
    @Path("logon")
    public Response logon(@QueryParam("username") String username,
                          @QueryParam("password") String password)
    {
    	System.out.println("here into logon,username: "+username+"pwd:"+password);
    	User user = null;
    	try {
			System.out.println("password:"+rsa.decrypt(Base64.decode(password.getBytes())));
			user = repo.Authenticate(username, rsa.decrypt(Base64.decode(password.getBytes())));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if (user != null)
        {
            MyRSA myRSA = new MyRSA();
            String auth = null;
            try {
                auth = myRSA.encrypt(Integer.toString(user.uid).getBytes());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            
            if (auth != null)
            {
                return Response.ok().entity(auth).build();
            }
            else
            {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
            }
        }
        else
        {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Incorrect username/password")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

    @POST
    @Path("signup")
    public ProfileDTO signUp(@QueryParam("nickname") String nickname,
                          @QueryParam("password") String password,
                          @QueryParam("email") String email,
                          @QueryParam("usertype") String type,
                          @QueryParam("externalId") String externalId,
                          @QueryParam("gender") String gender)
    {
        boolean isBadRequest = false;
        System.out.println("into signup:"+nickname+ password+ email + type + externalId);
        UserType typeSignUp = UserType.fromString(type);
        
        if (typeSignUp == UserType.BANGBANG)
        {
           if (nickname == null || password == null || nickname.isEmpty() || password.isEmpty())
                isBadRequest = true;
        }
       else
        {
            if (externalId == null || externalId.isEmpty())
                isBadRequest = true;
        }

        if (isBadRequest)
        {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("username/password/emailAddress cannot be empty")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
       if(null != repo.getUserByEmail(email))
        {
           throw new WebApplicationException(Response
                   .status(Response.Status.CONFLICT)
                   .entity("Sign up failed for email exist")
                   .type(MediaType.TEXT_PLAIN)
                   .build());
        }
        User user = null;
		try {
			user = repo.Create(nickname, rsa.decrypt(Base64.decode(password.getBytes())), email,typeSignUp, externalId,gender);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (user == null)
            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Sign up failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        else
        {	
    	    Thread t = new Thread(new mailSenderThread(user.email,BYConstants.EMAIL_WELCOME_TITLE,BYConstants.EMAIL_WELCOME_CONTENT));
    		t.start();
    		ProfileDTO pdto = ProfileDTO.ConvertFromDBObject(user);       
          return pdto;
        }
    }
    @POST
    @Path("user/changePassword")
    @BangBangAuth
    public Response changePassword(@QueryParam("oldPassword") String oldPwd,
    		@QueryParam("newPwd") String newPwd,
    		@QueryParam("uid") int uid)
    {
        User user = repo.GetUserById(uid);

        if (user == null){
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Sign up failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
		try {
			if (!BCrypt.checkpw(rsa.decrypt(Base64.decode(oldPwd.getBytes())), user.password)){
			    throw new WebApplicationException(Response
			            .status(Response.Status.CONFLICT)
			            .entity("Sign up failed")
			            .type(MediaType.TEXT_PLAIN)
			            .build());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		    throw new WebApplicationException(Response
		            .status(Response.Status.CONFLICT)
		            .entity("Sign up failed")
		            .type(MediaType.TEXT_PLAIN)
		            .build());
		}
		try {
			// ResetPassword() will reset identifyCode, if we only change password, identify code should also be changed
			if(false == repo.ResetPassword(user.uid, rsa.decrypt(Base64.decode(newPwd.getBytes())))){
			    throw new WebApplicationException(Response
			            .status(Response.Status.NOT_MODIFIED)
			            .entity("Sign up failed")
			            .type(MediaType.TEXT_PLAIN)
			            .build());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    throw new WebApplicationException(Response
		            .status(Response.Status.NOT_MODIFIED)
		            .entity("Sign up failed")
		            .type(MediaType.TEXT_PLAIN)
		            .build());
		}
       return Response.ok().entity("success").build();

    }
    @POST
    @Path("user/ResetPasswordByIdCode")
    public Response ResetPasswordByidentifyCode(@QueryParam("email") String email,
    		@QueryParam("pwd") String pwd,
    		@QueryParam("identifyCode") String identifyCode)
    {
        User user;
        // check if email is exist
        user = repo.getUserByEmail(email);
        if(null == user || identifyCode == null)
        {
           throw new WebApplicationException(Response
                   .status(Response.Status.BAD_REQUEST)
                   .entity("reset password failed for email not exist")
                   .type(MediaType.TEXT_PLAIN)
                   .build());
        }
       // check if identifyCode is matched
        
       if(!identifyCode.equals(user.identifyCode)){
           throw new WebApplicationException(Response
                   .status(Response.Status.CONFLICT)
                   .entity("reset password  failed for identify code not match")
                   .type(MediaType.TEXT_PLAIN)
                   .build());
        }
       try {
		if(false == repo.ResetPassword(user.uid, rsa.decrypt(Base64.decode(pwd.getBytes())))){
		       throw new WebApplicationException(Response
		               .status(Response.Status.NOT_MODIFIED)
		               .entity("reset password  failed for update DB fail")
		               .type(MediaType.TEXT_PLAIN)
		               .build());
		   	}
       } catch (Exception e) {
		// TODO Auto-generated catch block
    	   e.printStackTrace();
	       throw new WebApplicationException(Response
	               .status(Response.Status.NOT_MODIFIED)
	               .entity("reset password  failed for update DB exception")
	               .type(MediaType.TEXT_PLAIN)
	               .build());
       }
        // update password successful
       return Response.ok().entity("success").build();
    }
    @POST
    @Path("user/reqIdentifyCode")
    public Response reqIdentifyCode(@QueryParam("email") String email)
    {
        User user;
        String identifyCode;
        // check if email is exist
        user = repo.getUserByEmail(email);
        if(null == user)
        {
           throw new WebApplicationException(Response
                   .status(Response.Status.BAD_REQUEST)
                   .entity("reqIdentifyCode failed for email not exist")
                   .type(MediaType.TEXT_PLAIN)
                   .build());
        }
       if(user.identifyCode == null){
    	   // if identifyCode generate once, do not regenerate it 
           Random ran1=new Random();
           identifyCode= ""+ran1.nextInt(10)+ran1.nextInt(10)+ran1.nextInt(10)+ran1.nextInt(10);

           if(false == repo.updateIdentifyCode(user.uid,identifyCode)){
              throw new WebApplicationException(Response
                       .status(Response.Status.NOT_MODIFIED)
                       .entity("reqIdentifyCode  failed for update DB fail")
                       .type(MediaType.TEXT_PLAIN)
                       .build());
           }	
        }else
        {
        	identifyCode = user.identifyCode;
        }
	    Thread t = new Thread(new mailSenderThread(user.email,BYConstants.EMAIL_RESET_PASSWORD_TITLE,"您好！\n" +
	    		"    您的密码重置码是:"+identifyCode +"\n"+
	    	    "    请您尽快重置您的密码！\n"));
		t.start();
       return Response.ok().entity("success").build();

    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user/{id}")
    @BangBangAuth
    public  ProfileDTO getUserbyId(@PathParam("uid") int uid) {
        System.out.println("into getUserbyId:"+uid);

        User user = repo.GetUserById(uid);
        if (user == null){
            throw new WebApplicationException(Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Sign up failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
       }else
        {
        	System.out.println("get::"+ user.email+user.nickName);
        	ProfileDTO pdto = ProfileDTO.ConvertFromDBObject(user);
          return pdto;
        }     
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user/getuserbyname")
    @BangBangAuth
    public  ProfileDTO getUserbyUserName(@QueryParam("userName") String userName) {
        System.out.println("into getUserbyUserName:"+userName);

        User user = repo.GetUserByUserName(userName);
        if (user == null){
        	System.out.println("get user by email failed");

            throw new WebApplicationException(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Sign up failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }

        else
        {
        	System.out.println("get::"+ user.email+user.nickName);
        	ProfileDTO pdto = ProfileDTO.ConvertFromDBObject(user);
          return pdto;
        }
        
    }
    @GET
    @Path("user")
    @BangBangAuth
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUser()
    {
        return repo.GetAllUser();
    }

    @PUT
    @Path("user/update")
    @BangBangAuth
    @Produces(MediaType.APPLICATION_JSON)
    public ProfileDTO updateUser(@QueryParam("uid") int uid,
    		@QueryParam("nickName") String nickname,
          @QueryParam("email") String email,
          @QueryParam("gender") String gender,
          @QueryParam("city") int city,
          @QueryParam("birthDay") String birthday,
          @QueryParam("age") int age,
          @QueryParam("image") String image
          )
    {            
    	
    	File fimage = null;
    	
        System.out.println("into updateUser");

    	Date birthDay =null;
    	try {
    		birthDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(birthday);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("update failed for wrong date format")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
		}
		String timage=headImagePath+"tmp"+String.valueOf(uid)+".jpg";
		fimage = new File(timage);
		
    	HashMap<String,Object> map = new HashMap<String,Object>();
    

    	if(image.length() == 0 && true == fimage.exists()){
    		// if first time to set image && update(ftp) tmpuid.jpg to server
        	image=headImagePath+String.valueOf(uid)+".jpg";
        	map.put("head_photo_modify_at", new Date());
    	}else if(true == fimage.exists()){
    		// if tmpuid.jpg exist,means image updated
        	map.put("head_photo_modify_at", new Date());

    	}
    
    	map.put("uid", uid);
    	map.put("nickName", nickname);
    	map.put("email", email);
    	map.put("gender", gender);
    	map.put("city", city);
    	map.put("birthDay", birthDay);
    	map.put("age", age);
    	map.put("image", image);
        
    	if(false == repo.Update(map)){
    		throw new WebApplicationException(Response
                    .status(Response.Status.NOT_MODIFIED)
                    .entity("update failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build());
    	}
    	else{
            User user = repo.GetUserById(uid);
            if (user == null){
                throw new WebApplicationException(Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Sign up failed")
                        .type(MediaType.TEXT_PLAIN)
                        .build());
           }else
            {
            	System.out.println("get::"+ user.email);
            	ProfileDTO pdto = ProfileDTO.ConvertFromDBObject(user);
            	// if tmpuid.jpg exist, rename it to uid.jpg
        		if(true == fimage.exists())
            	{
        			System.out.println("rename from:"+fimage+"to :"+image);
            		System.out.println("rename result:"+fimage.renameTo(new File(image)));
            	}
              return pdto;
            }    	
    	}
    }
    @POST
    @Path("user/suggestion")
    @BangBangAuth
    public Response suggestion(@QueryParam("uid") int uid,
            @QueryParam("suggestion") String suggestion)
    {
        System.out.println("into suggestion:"+ uid + suggestion);

        if (repo.insertSuggestion(uid,suggestion) == true)
        {
            return Response.ok().entity("successful").build();
        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }
    }
    @POST
    @Path("updateclientid")
    @BangBangAuth
    public Response updateClientId(@QueryParam("uid") int uid,
                          @QueryParam("clientid") String clientid)
    { 
    	System.out.println("updateClientId:"+uid+" "+clientid);
    	if(uid == 0 || clientid == null)
    	{
           throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
    	}
    	if(true == repo.updateClientId(uid,clientid))
        {
    		return Response.ok().entity("success whatever").build();
        }
        else
        {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }
    }
    @DELETE
    @Path("User/{id}")
    @BangBangAuth
    public boolean deleteUser(@PathParam("id") int userId)
    {
        return repo.Delete(userId);
    }
    
    
    
    /* 20140105 followed is the give up codes, always get null when multipart data receviced, I think it's a server side bug
     * but don't know how to fix, so just use ftp to upload image  */
    @POST
    @Path("user/image")
 //   @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
//FormDataMultiPart need @Context, otherwise no response
      //  public boolean uploadMarkup(MultiPart request ){  

   //   public boolean uploadMarkup( @Context FormDataMultiPart request ){  
   	      public boolean uploadMarkup(
       		@FormDataParam("file") InputStream uploadedInputStream){
     //   	    @FormDataParam("file") FormDataContentDisposition fileDetail) {
 //   public boolean uploadMarkup(@Context HttpServletRequest request) {
    	System.out.println("dsafdsf"+uploadedInputStream);
    	return true;
      //  Object[] data = uploadService.getParametersAndFiles(request);
      //  ...
    }


}