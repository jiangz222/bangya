/**
 *
 */
package com.bangbang.webapi.server.repository;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.bangbang.webapi.server.auth.BCrypt;
import com.bangbang.webapi.server.model.ProfileDTO;
import com.bangbang.webapi.server.model.User;
import com.bangbang.webapi.server.model.UserType;

/**
 * @author wisp
 *
 */
public class UserRepository implements IUserRepository {
	public DBRepositoryOperator dbOp = new DBRepositoryOperator();

    static List<User> userList = null;
    static int lastId = 0;

    public UserRepository()
    {
        if (userList == null)
        {
            userList = new LinkedList<User>();

            User user = new User();
            user.uid = 1;
            user.nickName = "wisp";
            user.province = 1;
            user.city = 1;
            user.create_at = new Date();
            user.modify_at = new Date();
            user.gender = "m";
            user.age = 30;
            user.description = "no comments";
            user.image = null;
            userList.add(user);
            lastId = 1;

            User user2 = new User();
            user2.uid = 2;
            user2.nickName = "lemon";
            user.province = 1;
            user.city = 1;
            user2.create_at = new Date();
            user2.modify_at = new Date();
            user2.gender = "m";
            user2.age = 30;
            user2.description = "no comments";
            user2.image = null;
            userList.add(user);
            lastId = 2;
        }
    }

    @Override
    public User Authenticate(String username, String password) {
   	 	User userFromQuery = new User();
    	try {
    		userFromQuery = (User)dbOp.queryUserByName(username,userFromQuery);
    		if(null == userFromQuery){
    			return null;
    		}
    		// Check that an unencrypted password matches one that has
    		// previously been hashed
    		if (BCrypt.checkpw(password, userFromQuery.password))
    			return userFromQuery;
    		else
    			return null;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    @Override
    public User getUserByEmail(String email)
    {
   	 	User user = new User();
   	 	
		try {
			user = (User)dbOp.queryOneObjectbySingleCondition("email",email,"User",user);
			if(null == user) 
			{//email not exist
				System.out.println("fail to find user by email: "+email);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user = null;
		}
    	return user;
    }
    // Return user id
    @Override
    public User Create(String nickname, String password, String email, UserType type, String externalId, String gender) {
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("nickName", nickname);
    	map.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));
    	map.put("type", type.getValue());
    	map.put("email", email);
    	map.put("externalId", externalId);
    	map.put("create_at", new Date());
    	map.put("modify_at", new Date());
    	map.put("gender", gender);
    	User user = new User();
		try {
			long newUid = (long)dbOp.insertOnelineDB(map, "User");
			user = (User)dbOp.queryOneObjectbySingleCondition("uid",newUid,"User",user);
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

    }
    @Override
    // only support email as username now
    public User GetUserByUserName(String userName)
    {
   	 	User user = new User();
   	 	
		try {
			user = (User)dbOp.queryOneObjectbySingleCondition("email",userName,"User",user);
			if(null == user) 
			{
				System.out.println("fail to get user  by Uname: "+userName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user = null;
		}
    	return user;
    }


    @Override
    public List<User> GetAllUser() {
        // TODO Auto-generated method stub
        return userList;
    }

    @Override
    public User GetUserById(int uid) {
   	 	User user = new User();
   	 	
		try {
			user = (User)dbOp.queryOneObjectbySingleCondition("uid",uid,"User",user);
			if(null == user) 
			{//email not exist, fine to create new user
				System.out.println("fail to get user by uid: "+uid);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user = null;
		}
    	return user;
    }

    @Override
    public boolean ResetPassword(int uid, String newPassword) {
    	int updateRowCount = 0;//useless here

    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("password", BCrypt.hashpw(newPassword, BCrypt.gensalt()));
    	map.put("identifyCode", null);// reset identifyCode
    	map.put("uid", uid);
		try {
			dbOp.updateOneRowDB(map, "User", "uid",updateRowCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
       return true;
    }

    @Override
    public boolean Update(HashMap<String,Object> map) {
    	int updateRowCount = 0;//useless here
		try {
			return dbOp.updateOneRowDB(map, "User", "uid",updateRowCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    @Override

   public boolean insertSuggestion(int uid,String suggestion)
    {
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("uid", uid);
    	map.put("suggestion", suggestion);
    	map.put("create_at", new Date());

		try {
			dbOp.insertOnelineDB(map, "Suggestion");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	return true;
    }

    @Override
    public boolean Delete(int id) {
        boolean ret = true;
        try {
            for(int i = 0; i < userList.size(); i++)
            {
                if (userList.get(i).uid == id)
                {
                    userList.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }

	@Override
	public boolean updateClientId(int uid, String clientId) {
		// TODO Auto-generated method stub
		//update if uid already exist,insert if uid not exist
		int updateRowCount = 0;
		
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("uid", uid);
    	map.put("cid", clientId);
    	map.put("modifyTime", new Date());
		try {
	    	if(false == dbOp.updateOneRowDB(map, "uidcidmap", "uid",updateRowCount)
	    	&& 0 == updateRowCount)
	    	{
	    		dbOp.insertOnelineDB(map, "uidcidmap");
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	return true;	
    }
	@Override
	public boolean updateIdentifyCode(int uid, String identifyCode)
	{
        // generate random identify code 
 		int updateRowCount = 0;//useless here

   		HashMap<String,Object> map = new HashMap<String,Object>();
   		map.put("uid", uid);
   		map.put("identifyCode", identifyCode);
		try {
			dbOp.updateOneRowDB(map, "User", "uid",updateRowCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

}
