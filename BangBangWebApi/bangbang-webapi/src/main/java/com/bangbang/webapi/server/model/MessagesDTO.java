package com.bangbang.webapi.server.model;

import java.util.Date;

/**
 * Created by wisp on 3/29/14.
 */
public class MessagesDTO {
    public int msgId;
    public int jobId;
    public int ownerUid;
    public String content;
    public Date createTime;
    public int type;		//消息类型：纯文本、表情+文本、图片、语音
    public static MessagesDTO ConvertFromDBObject(MessagesDAO msg)
    {
    	MessagesDTO msgDTO = new MessagesDTO();
    	msgDTO.msgId = msg.getmsgId();
    	msgDTO.jobId = msg.getJobId();
    	msgDTO.ownerUid = msg.getOwnerUid();
    	msgDTO.content = msg.getContent();
    	msgDTO.createTime = msg.getCreateTime();
    	msgDTO.type = msg.getType();
    	return msgDTO;
    }
}
