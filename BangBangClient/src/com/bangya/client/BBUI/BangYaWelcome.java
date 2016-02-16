package com.bangya.client.BBUI;


import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.model.SystemDTO;
import com.bangya.client.model.User;
import com.joeapp.bangya.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class BangYaWelcome  extends Activity implements InnerCommInterface  {
	private String TAG = "BBWelcome";
	private boolean bIsNeedLogin = true;
	private int durationOfAnimate = 5500;
   private  User loginUserInfo =new User();
   BaseUtil bu;
   private boolean needIntroActivity=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo_Light_NoActionBar);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bangya_welcome);
		FileUtil.createBangYaDIR();
		 bu = new BaseUtil(this);
		 BaseUtil.innerComm.addCurrentActivity(this);
		 
		if(!bu.getIntroPreference(this))
		{// first time start this APP, go to introduction activity without get user info
			durationOfAnimate = 1000;
			bu.setIntroPreferenceDone(this);
			needIntroActivity = true;
		}else{
			// not first time
			loginUserInfo = bu.getTokenFromPreference(this);
			if(loginUserInfo.getUserName().equals("")|| loginUserInfo.getPassWord().equals(""))
			{//  do not have username and password
				Log.e(TAG, "empty username "+loginUserInfo.getUserName()+"or token"+loginUserInfo.getPassWord());
				bu.toastShow(this,"请先登录或注册吧");
				durationOfAnimate = 2000;	//no need to connect to server,so go quickly
				bIsNeedLogin = true;
			}else
			{//get token from preference and store in user.password here,rewrite to normal password when get userinfo from server
				Log.e(TAG, " username: "+loginUserInfo.getUserName()+" token:"+loginUserInfo.getPassWord());
			//	webappclient.Logon(loginUserInfo.getUserName(),loginUserInfo.getPassWord());
				WebAppClient.MY_AUTH = loginUserInfo.getPassWord();
	    		BaseUtil.innerComm.webappclient.getUserInfoByUserName(loginUserInfo.getUserName());
			}
		}
		 

		new Handler().postDelayed(new Runnable(){   
		    public void run() {   
		    	Intent it;
		    	if(needIntroActivity){
		    		it = new Intent(BangYaWelcome.this,IntroductionActivity.class);
		    	}else{
		    		it	= new Intent(BangYaWelcome.this,HomeActivity.class);	
			    	if(true == bIsNeedLogin){
			    		it.putExtra("IsLogin", false);
			    		// we do not have UID here, so can not get correct notifyFlag from preference
			    		// change the correct value when login or register
			    		Setting.isNotifyEnable = true;
					}else
					{
		            	it.putExtra("IsLogin", true);
			    		//check if notify enable
		    		   Setting.isNotifyEnable =bu.getNotifyPreference(BangYaWelcome.this);	

					}
		    	}
        		BaseUtil.innerComm.delCurrentActivity(BangYaWelcome.this);
        		finish();
				startActivity(it);

		    }   
		 }, durationOfAnimate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bang_bang_welcome, menu);
		return true;
	}
	public void processMessage(Message msg)
	{
		switch(msg.what)
		{
			case MessageId.CONNECT_SERVER_ERROR:
			{
				bu.toastShow(this,"网络连接失败，请恢复后再试");
			//	Log.e(TAG, "connect to server failed");
				bIsNeedLogin = true; /* log on failed because of server connect fail, goto log on activity*/
				break;
			}
			case MessageId.LOGIN_FAIL_UI:
			{
				bu.toastShow(this,"默认用户名密码错误，请重新登录");
				Log.e(TAG, "log on failed");
				bIsNeedLogin = true; /* log on failed because of username pwd mismatch, goto log on activity*/
				break;
			}
			case MessageId.LOGIN_SUCCESS_UI:
			{//never called
        		BaseUtil.innerComm.webappclient.getUserInfoByUserName(loginUserInfo.getUserName());
				break;
			}
			case MessageId.GET_USER_BY_NAME_SUCCESS_UI:
			{
				bIsNeedLogin = false;
			   User user = new User();
			   user = (User)msg.obj;
			   BaseUtil.setSelfUserInfo(user);
				BaseUtil.setVersionNeedUpdateFlag(false);
			   // update system info(e.g. version updated needed checking) when APP start
				BaseUtil.innerComm.webappclient.getSystemInfo();
			}
			break;
			case MessageId.GET_USER_BY_NAME_FAIL_UI:
			{
				bu.toastShow(this,"获取用户信息失败，请重新登录");
        		bIsNeedLogin = true;
    		//	setContentView(R.layout.login);
			}
			break;
			case MessageId.GET_SYSTEM_INFO_SUCCESS:
			{
				BaseUtil.setSystemInfo((SystemDTO)msg.obj);
				if(BaseUtil.isVersionUpdateNeeded(this,(SystemDTO)msg.obj)){
					BaseUtil.setVersionNeedUpdateFlag(true);
				}else
				{
					BaseUtil.setVersionNeedUpdateFlag(false);
				}
				break;
				
			}
			case MessageId.GET_SYSTEM_INFO_FAIL:
			{
			// no need to show user toast when only need to check version here
			//	bu.toastShow(this, "获取版本信息失败，请稍后再试");
				Log.e(TAG, "get system info failed in welcome activity");
				break;
			}
			default :
				Log.e(TAG,"unknow msg"+msg.what);
		}
	}


}
