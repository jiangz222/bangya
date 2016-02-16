package com.bangya.client.BBUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bangya.client.Util.Constants;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.comm.SFtpRequest;
import com.bangya.client.comm.InnerCommAdapter;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.location.SimpleLocationInfo;
import com.bangya.client.model.JobChangedDetail;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.PushChangeClientInfo;
import com.bangya.client.model.PushMsgDTO;
import com.bangya.client.model.SystemDTO;
import com.bangya.client.model.User;
import com.igexin.sdk.PushManager;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public  class BaseUtil{
	private final String TAG = "BaseUtil";
	public static InnerCommAdapter innerComm = null;
	public static SystemDTO systemInfo = null;

	private static User SelfUserInfo = new User();
	private final String BangYaPrefrenceName = "BYPrefrence";
	Bitmap headbm = null;
	public static Toast gToast = null;
	public static String clientId = null;
	public BaseUtil(Context ct)
	{		
		if(innerComm == null){
			innerComm = new InnerCommAdapter(ct);
		}
		
	}
	public BaseUtil()
	{		
	}
	
	public static User getSelfUserInfo()
	{
		return SelfUserInfo;
	}
	public static SystemDTO getSystemInfo()
	{
		return systemInfo;
	}
	public static void setSelfUserInfo(User selfinfo)
	{
		SelfUserInfo = selfinfo;
	}
	public static void setSystemInfo(SystemDTO systemInfoInput)
	{
		systemInfo = systemInfoInput;
		
	}


	public User getPreference(Context ct)
	{
		User userinfo = new User();
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		userinfo.setUserName(sp.getString("username",""));
		userinfo.setPassWord(sp.getString("token",""));
		Log.i(TAG, "get preference:"+sp.getString("username","")+"token:"+sp.getString("token",""));
		return userinfo;
	}
	public void setSharePreferencesForToken(String UserName, String token,Context ct)
	{ 
		Log.e(TAG, "into preference:"+UserName+" token:"+token);
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username",UserName);
		editor.putString("token", token);
		editor.commit();
	}
	public boolean getNotifyPreference(Context ct)
	{
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		boolean nt =  sp.getBoolean(BaseUtil.getSelfUserInfo().getUserId()+"notify",true);
		Log.e(TAG, "getNotifyPreference:"+nt);
		return nt;
	}
	public  void setSharePreferencesForNotify(boolean flag,Context ct)
	{ 
		Log.e(TAG, "setSharePreferencesForNotify:"+flag);
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(BaseUtil.getSelfUserInfo().getUserId()+"notify",flag);
		editor.commit();
	}
	public boolean getIntroPreference(Context ct)
	{
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		boolean nt =  sp.getBoolean("intro",false);
		return nt;
	}
	public  void setIntroPreferenceDone(Context ct)
	{ 
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("intro",true);
		editor.commit();
	}
	public User getTokenFromPreference(Context ct)
	{
		User loginUserInfo	= new User();
		loginUserInfo = getPreference(ct);
		return loginUserInfo;
	}
	public void storeHeadPhotoModifyTime(Context ct,Date date,int uid)
	{
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		editor.putString(String.valueOf(uid),time);
		editor.commit();
		Log.i(TAG, "store time:"+time);

	}
	public Date getHeadPhotoModifyTimeByUid(Context ct,int uid)
	{
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		String mt =  sp.getString(String.valueOf(uid),"");
		Date modifyTime = null;
		if(mt.equals(""))
		{
			Log.i(TAG,"not found modify time for uid:"+uid);
			return modifyTime;
		}
		try {
			modifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mt);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "getHeadPhotoModifyTimeByUid:"+modifyTime);
		return modifyTime;
	}

	public void toastShow(Context context,String toastString)
	{
		if(gToast == null)
		{
			gToast  = Toast.makeText(context,toastString,Toast.LENGTH_LONG);
		}else
		{
			gToast.setText(toastString);
		}
		gToast.show();
	}
	public void checkHeadPhotoUpdated(Context ct,List<JobDTO> bangbangjoblist,int direction)
	{
	    Thread t = new Thread(new SFtpRequest(ct,SFtpRequest.imagePathOnSvr,bangbangjoblist,direction));
		t.start();
	}
	public static void getFileFromSvr(String dst,String src,String fileNameOnSever){
	    Thread t = new Thread(new SFtpRequest(src,dst,fileNameOnSever,SFtpRequest.GET));
		t.start();	
	}

    public void ftpImage(String filePath){
    	//Thread t = new Thread(new FtpRequest(filePath,FtpRequest.imagePathOnSvr));
    	Thread t = new Thread(new SFtpRequest(filePath,SFtpRequest.imagePathOnSvr,"tmp"+BaseUtil.getSelfUserInfo().getUserId()+".jpg",SFtpRequest.PUT));
    	t.start();
    	System.out.println("ImageReq Done");
    }
    public static boolean isWifiConnected(Context context){
        final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        if(wifi.isAvailable())
            return true;
        else
            return false;
    }

	public boolean checkPasswordValid(String pwd,Context ct){
		if(null == pwd || "".equals(pwd))
		{
			Log.e(TAG, "empty input pwd is not allowed");
			toastShow(ct,"密码不能为空");
        	return Constants.BB_FALSE;
		}
		if(pwd.length() < 6)
		{
			Log.e(TAG, "empty input pwd is not allowed");
			toastShow(ct,"密码必须为超过5位的数字、字母组合");
        	return Constants.BB_FALSE;
		}
		if(!((pwd.matches(".*[A-Z].*")||pwd.matches(".*[a-z].*")) && pwd.matches(".*[0-9].*"))) 
		{
			Log.e(TAG, "pwd not contain char and number");
			toastShow(ct,"密码必须为超过5位的数字、字母组合");
        	return Constants.BB_FALSE;
		}
		return Constants.BB_TRUE;
	}
	public boolean checkEmailVaild(String email,Context ct){ 
		if(null == email || "".equals(email))
		{
			Log.e(TAG, "empty input email is not allowed ");
			toastShow(ct,"请输入邮箱地址");
        	return Constants.BB_FALSE;
		}
		Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配  
       Matcher m = p.matcher(email);  
       if(!m.matches())  
       {
			Log.e(TAG, "invalide email"+email);
			toastShow(ct,"请输入有效的邮箱地址");
    	   return Constants.BB_FALSE;
       }
       return Constants.BB_TRUE;
	}
	public static void setVersionNeedUpdateFlag(boolean value){
		PushReceiver.needVersionUpdate = value;
	}
	public static boolean getVersionNeedUpdateFlag(){
		return PushReceiver.needVersionUpdate;
	}
	public static boolean isVersionUpdateNeeded(Context ct,SystemDTO systemInfo){
		PackageInfo packageInfo;
		try {
			packageInfo = ct
					.getPackageManager()
					.getPackageInfo(ct.getPackageName(), 0);
			String localVersion = packageInfo.versionName;
			if(!(systemInfo.getLatestClientVersion().equals(localVersion))){
				return true;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static String getCurrentVersion(Context ct){
		PackageInfo packageInfo;
		try {
			packageInfo = ct
					.getPackageManager()
					.getPackageInfo(ct.getPackageName(), 0);
			String localVersion = packageInfo.versionName;
 			return localVersion;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "未知";
	}
	public void setLocationToPreference(Context ct,SimpleLocationInfo location){
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("latitude", String.valueOf(location.getLatitude()));
		editor.putString("longtitude", String.valueOf(location.getLongitude()));
		editor.commit();
	}
	public SimpleLocationInfo getLocationFromPreference(Context ct){
		SimpleLocationInfo location = new SimpleLocationInfo();
		SharedPreferences sp = ct.getSharedPreferences(BangYaPrefrenceName,Context.MODE_PRIVATE);
		String latitude =  sp.getString("latitude","");
		String longitude =  sp.getString("longtitude","");
		if(latitude.equals("")||longitude.equals("")){
			// get nothing, only fake one
			Log.i(TAG,  "fake location now!");
			location.setLongitude(121.407957);
			location.setLatitude(31.107637);
		}
		else{
			Log.i(TAG,  "use old location now!");
			location.setLatitude(Double.parseDouble(latitude));
			location.setLongitude(Double.parseDouble(longitude));
		}
		Log.e(TAG,"get lat:"+location.getLatitude()+"get long:"+location.getLongitude());
		return location;
	}
}
