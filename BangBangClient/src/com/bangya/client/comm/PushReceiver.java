package com.bangya.client.comm;

import java.io.IOException;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.model.PushChangeClientInfo;
import com.bangya.client.model.PushMsgDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
/**
 * 
 * @author root
 * make sure that everything we try to access in PushReceiver
 * it may be null if BangYaClient is not started!
 * so better we access only static in PushReceiver and then other Activity use it in static way
 */
public class PushReceiver extends BroadcastReceiver {
	private String TAG="PUSH_REC";
	public static PushChangeClientInfo PCCInfo = new PushChangeClientInfo();
	public static boolean needVersionUpdate=false;
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.i("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		

		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
			
			if (payload != null) {
				String data = new String(payload);
				Log.i(TAG, "recevie push msg:"+data);
        		ObjectMapper objectMapper =new ObjectMapper();
        		PushMsgDTO PushMsg = null;
					try {
						PushMsg = objectMapper.readValue(data, PushMsgDTO.class);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			if(PushMsg == null)
        			{
        				Log.e(TAG, "get error of Push data DTO:"+data);
        				return;
        			}
        			Message msg = new Message();

        			switch(PushMsg.pushType)
        			{
        				case CHAT_MSG:
        					msg.obj = PushMsg;
        					msg.what = MessageId.PUSH_CHAT_MSG;
        					InnerCommAdapter.sendMessage(msg);
        					if(PushReceiver.PCCInfo != null){
        						PushReceiver.PCCInfo.addClientInfo(PushMsg);
        					}
        					BangYaNotification.sendNotification(context,"您的帮助有新的评论");
        				break;
        				case JOB_STATUS:
        					if(PushReceiver.PCCInfo != null){
        						PushReceiver.PCCInfo.addClientInfo(PushMsg);
        					}
        					BangYaNotification.sendNotification(context,"您的帮助有更新");
        					msg = new Message();
        					msg.what = MessageId.PUSH_JOB_STATUS;
        					InnerCommAdapter.sendMessage(msg);
            			break;	
        				case VERSION_UPDATE:
        					Log.i(TAG, "receive puhsed version_update msgs");
        					BaseUtil.setVersionNeedUpdateFlag(true);
        					break;
            			case OTHER:
                		break;
        			}
        		
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			/* 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，
			以便日后通过用户帐号查找CID进行消息推送*/
			String cid = bundle.getString("clientid");
			if(BaseUtil.innerComm!=null &&  null != BaseUtil.innerComm.webappclient){
				Log.d(TAG,"update clientid:"+cid);
				BaseUtil.clientId = cid;
				BaseUtil.innerComm.webappclient.updateClientId(BaseUtil.getSelfUserInfo().getUserId(),cid);
			}else{
				Log.d(TAG,"not update clientid because of uninited BaseUtil:"+cid);

			}

			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "taskid = " + taskid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "result = " + result);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);*/
			break;
		default:
			break;
		}
	}


}
