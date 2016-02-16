package com.bangbang.webapi.server.repository;

import java.util.HashMap;
import java.util.List;

import com.bangbang.webapi.server.model.*;

public interface IUserRepository {
    List<User> GetAllUser();
    User GetUserById(int uid);
    User Create(String username, String password, String email,UserType type, String externalId,String gender);
    public User getUserByEmail(String email);
    public User GetUserByUserName(String userName);
    boolean Delete(int uid);
    boolean Update(HashMap<String,Object> map);
    boolean updateClientId(int uid,String clientId);
    // method for authenticate
    User Authenticate(String username, String password);
    boolean ResetPassword(int uid, String newPassword);
	boolean insertSuggestion(int uid, String suggestion);
	boolean updateIdentifyCode(int uid, String identifyCode);
}