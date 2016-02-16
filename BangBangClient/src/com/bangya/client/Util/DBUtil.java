package com.bangya.client.Util;

import com.bangya.client.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtil extends SQLiteOpenHelper {
	private final static String DB_NAME = "BBclientDate";
	private final String USER_INFO_TABLE = "UserInfoTbl";
	private final String TAG="DBUtil";
	
	public DBUtil(Context context){
	//第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类  
		this(context, DB_NAME, null, 11);
	}

	public DBUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}


	public DBUtil(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub 
		//在调用getWritableDatabase或者getReadableDatabase 时，如果数据库不存在，自动创建后调用本函数
		String sql="create table "+USER_INFO_TABLE+
				"( user_id integer primary key autoincrement," +
				" user_name varchar2(20)," +
				" sex varchar2(2)," +
				" head varchar2(100)," +
				" age varcahr2(1)," +
				" points integer(1)," +
				" timestamp varchar2(20)," +
				" email varchar2(50))";
		try{
		db.execSQL(sql);
		}
		catch(SQLException e)
		{
			Log.e(TAG, "create fail "+e+USER_INFO_TABLE+sql);
		}	
		Log.i(TAG,"create db table"+USER_INFO_TABLE+sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//当DBUtil中的数据库版本发生变化，本函数被调用
		Log.i(TAG, "update db table ,old ver: "+oldVersion+"new ver: "+newVersion);
	    try {
            db.execSQL("drop table if exists "+DB_NAME);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	public User queryUserInfo(String userid)
	{
		User userinfo = null;
	    SQLiteDatabase db=getReadableDatabase();
	    Cursor cursor = db.rawQuery("select * from"+USER_INFO_TABLE+"where user_id='"+userid+"'", null);
	    //如果游标为空（查找失败）或查到的信息数位0，返回null
        if(cursor==null || cursor.getCount()==0 || cursor.getCount() > 1){
            Log.e(TAG, "queryUserInfo() exception:cursor null or cursor count: "+cursor.getCount());
            db.close();
            return null;
        }
        
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	userinfo = new User();
        	userinfo.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        	userinfo.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
        //	userinfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            userinfo.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            userinfo.setPoints(cursor.getInt(cursor.getColumnIndex("points")));
   //         userinfo.setTimeStamp(cursor.getInt(cursor.getColumnIndex("timestamp")));
            userinfo.setImageHead(cursor.getString(cursor.getColumnIndex("head")));
            userinfo.setEmail(cursor.getString(cursor.getColumnIndex("email")));
/*
        	Log.i(TAG, "queryUserInfo() success: userid: "+ userinfo.getUserId()
        			+"username1: "+userinfo.getUserName()
        			+"sex: "+userinfo.getSex()
        			+"age: "+userinfo.getAge()
        			+"points: "+userinfo.getPoints()
        			+"timestamp: "+userinfo.getTimeStamp()
        			+"imagehead: "+userinfo.getTimeStamp()
        			+"imagehead: "+userinfo.getEmail());*/
        }
        else
        {            
        	Log.e(TAG, "queryUserInfo() exception: cursor isAfterLast");
        }
        db.close();
	    return userinfo;
	}
	public boolean updateUserInfo(User newuserinfo)
	{
	    SQLiteDatabase db=getReadableDatabase();
	    Cursor cursor = db.rawQuery("select * from"+USER_INFO_TABLE+"where user_id='"+newuserinfo.getUserId()+"'", null);
	    //如果游标为空（查找失败）或查到的信息数位0，返回null
        if(cursor==null || cursor.getCount()==0 || cursor.getCount() > 1){
            Log.e(TAG, "updateUserInfo: exception:cursor null or cursor count: "+cursor.getCount());
            db.close();
            return Constants.BB_FALSE;
        }
		ContentValues updates=new ContentValues();
		updates.put("user_name", newuserinfo.getUserName());
	//	updates.put("sex", newuserinfo.getSex());
		updates.put("head", newuserinfo.getImageHead());
		updates.put("age", newuserinfo.getAge());
		updates.put("points", newuserinfo.getPoints());
	//	updates.put("timestamp", newuserinfo.getTimeStamp());
		updates.put("email", newuserinfo.getEmail());

		String where="userid="+newuserinfo.getUserId();
		
		int numRowInfect=db.update("friend", updates, where, null);
		if(0 == numRowInfect || numRowInfect>1)
		{
			Log.e(TAG, "updateUserInfo: no infect or infect more than 1 row error: "+numRowInfect);
		}
		db.close();
		return Constants.BB_TRUE;
	}
	public String queryUserInfoTimeStamp(String userid)
	{
		String timestamp = null;
		Log.i(TAG,"into queryUserInfoTimeStamp 1");
	    SQLiteDatabase db=getReadableDatabase();
		Log.i(TAG,"into queryUserInfoTimeStamp 2");

	    Cursor cursor = db.rawQuery("select * from "+USER_INFO_TABLE+" where user_id='"+userid+"'", null);
	    //如果游标为空（查找失败）或查到的信息数位0，返回null
        if(cursor==null || cursor.getCount()==0 || cursor.getCount() > 1){
            Log.e(TAG, "queryUserInfoTimeStamp :cursor null or cursor count: "+cursor.getCount());
            db.close();
            return null;
        }
		Log.i(TAG,"into queryUserInfoTimeStamp 3");

        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	timestamp = new String();
        	timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
        	Log.i(TAG, "queryUserInfo() success: userid: "+ userid+"timestamp: "+timestamp);
        }
        else
        {            
        	Log.e(TAG, "queryUserInfo() exception: cursor isAfterLast");
        }
        db.close();
	    return timestamp;
	
	}
}
