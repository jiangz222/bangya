package com.bangbang.webapi.server.pushmanager;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;


import com.bangbang.webapi.server.model.BangYaPushType;
import com.bangbang.webapi.server.model.MessagesDTO;
import com.bangbang.webapi.server.model.PushMsgDTO;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class PushManager implements IPushManager {
	private  final String APPID = "HjwF6PCujhAnpmvSVlPx95";
	private  final String APPKEY = "2U8GPQGypm9RngDQ4IJVC3";
	private  final String MASTERSECRET = "sRNXMQjBwS6mQjZWzj83A5";
	//OpenService接口地址
	private  final String API = "http://sdk.open.api.igexin.com/apiex.htm";

	@Override
	public boolean pushTransmissionMsgToSingle(String clientId,String content){
		System.out.println("pushTransmissionMsgToSingle:"+clientId+" "+content);
		if(content == null)
		{
			return false;
		}
		IIGtPush push = new IGtPush(API, APPKEY, MASTERSECRET);
		try {
			SingleMessage message = new SingleMessage();
			//通知模版:支持TransmissionTemplate、LinkTemplate、NotificationTemplate,此处以
			//TransmissionTemplate为例
			TransmissionTemplate msgTemplate = new TransmissionTemplate();
			//msgTemplate.setTitle("fdsafds");
		//	msgTemplate.setText("1212");
		//	msgTemplate.setLogo("push.png");
			msgTemplate.setAppId(APPID);
			msgTemplate.setAppkey(APPKEY);
			msgTemplate.setTransmissionContent(content);
		
			//收到消息是否立即启动应用,1为立即启动,2则广播等待客户端自启动
			//msgTemplate.setTransmissionType(3);
			message.setData(msgTemplate);
		
			//用户当前不在线时,是否离线存储,可选
			message.setOffline(true);
			//离线有效时间,单位为毫秒,可选
			message.setOfflineExpireTime(72 * 3600 * 1000); 
			Target target1 = new Target();
			target1.setAppId(APPID);
			target1.setClientId(clientId);
			IPushResult ret = push.pushMessageToSingle(message, target1);
			System.out.println(ret.getResponse().toString());
		} catch (Exception e) {
		e.printStackTrace();
		return false;
		}
		return true;
	}

	@Override
	public String constructPushMessage(int pushFromUid, BangYaPushType pt, int jobId,
			int direction, MessagesDTO msg) {
		// TODO Auto-generated method stub
	    PushMsgDTO pushmsg = new PushMsgDTO();
	    pushmsg.direction = direction;
	    pushmsg.jobId = jobId;
	    pushmsg.msgDTO = null;
	    pushmsg.pushType = pt;
	    pushmsg.pushFromUid = pushFromUid;
	    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	    String json = null;
		try {
			json = ow.writeValueAsString(pushmsg);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return myObject.toString();
		System.out.println("push:"+json);
	    return json;
	}
}
