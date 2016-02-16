package com.bangbang.webapi.server.pushmanager;

import com.bangbang.webapi.server.model.BangYaPushType;
import com.bangbang.webapi.server.model.MessagesDTO;


public interface IPushManager {
	public boolean pushTransmissionMsgToSingle(String clientId,String content);
	public String constructPushMessage(int pushFromUid, BangYaPushType pt,int jobId,int direction,MessagesDTO msg);
}
