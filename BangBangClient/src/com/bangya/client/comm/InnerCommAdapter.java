package com.bangya.client.comm;

import java.io.File;
import java.util.LinkedList;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 *  get the current activity/fragment AND let them handle the msg from server
 *   
 *   because the pre-load of besides fragment:
 *   fragment findjob and newjob ,is seperated by other frament(trends)
 *   this make only one of them can alive in linklist when one of them is current activity
 *   if this two fragment is besides, may last in linklist is not the current activity,
 * @author zhiji
 *
 */
public class InnerCommAdapter {
	private String TAG="InnerCommAdapter";
	protected static LinkedList<InnerCommInterface> innerCommQue = new LinkedList<InnerCommInterface>();  
	public  WebAppClient webappclient = null;
	public static String clientId = null;
	public static Context ct;
	BaseUtil bu = new BaseUtil();
	public InnerCommAdapter(Context ct)
	{
		if(null == webappclient)
		{
			webappclient = new WebAppClient();
			webappclient.init(ct);
			/* initPushManager here */
		}
	}
	public void addCurrentActivity(InnerCommInterface activityInst) {
		Log.i(TAG, "in addCurrentActivity");
		
		if (!innerCommQue.contains(activityInst)){
			Log.i(TAG, "addCurrentActivity add "+activityInst);
			innerCommQue.add(activityInst);
		}
		else{
			Log.e(TAG, "class exsit which suppose not in list");
		}
		Log.i(TAG, "now the activity is "+ innerCommQue.getLast()+"activity number:"+innerCommQue.size());
	}
	public void delCurrentActivity(InnerCommInterface activityInst)
	{

			if(innerCommQue.contains(activityInst)){
				if(innerCommQue.getLast() != activityInst){
					Log.e(TAG,"delete the activity is not the last one");
				}
				Log.i(TAG,"remove the activity "+activityInst);

				innerCommQue.remove(activityInst);

			}
			else{
				Log.e(TAG,"not contain  the activity "+activityInst);

			}
		return;
	}
	public static void sendEmptyMessage(int what) {
		handler.sendEmptyMessage(what);
	}
	public static void sendMessage(Message msg) {
		handler.sendMessage(msg);
		
	}
	//Handler对象是静态的，则所有的子类都是共用同一个消息队列
	private static Handler handler = new Handler(){
		@Override
       public void handleMessage(Message msg) {
			switch(msg.what){
			case MessageId.UPDATE_CLIENT_ID_FAIL:
			{
				BaseUtil.innerComm.webappclient.updateClientId(BaseUtil.getSelfUserInfo().getUserId(), clientId);
				break;
			}
			case MessageId.APK_DOWN_LOAD_DONE:
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(FileUtil.APK_PATH+BaseUtil.getSystemInfo().getLatestClientName())), "application/vnd.android.package-archive");
				InnerCommAdapter.ct.startActivity(intent);
				break;
			}

			default:
			{	
				if(!innerCommQue.isEmpty()){
					Log.i("InnerCommAdapter","baseutil send msg:"+msg.what+"to->"+innerCommQue.getLast());
					innerCommQue.getLast().processMessage(msg);
				}
				else{
					Log.e("BaseUtil","UI activity innerCommQue is empty,drop msgid:!!"+msg.what);
				}
				break;
			}
			
       }
	}
	};

}
