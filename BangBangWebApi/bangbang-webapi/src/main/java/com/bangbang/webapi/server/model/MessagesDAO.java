package com.bangbang.webapi.server.model;

import java.util.Date;

/**
 * Created by wisp on 3/29/14.
 */
public class MessagesDAO {
    private int msgId;
    private int jobId;
    private int ownerUid;
    private String content;
    private Date createTime;
    private int type;		//消息类型：纯文本、表情+文本、图片、语音
    
    public int getmsgId()
    {
    	return this.msgId;
    }
    public int getJobId()
    {
    	return this.jobId;
    }
    public String getContent()
    {
    	return this.content;
    }    
    public int getOwnerUid()
    {
    	return this.ownerUid;
    }    
    public Date getCreateTime()
    {
    	return this.createTime;
    }    
    public int getType()
    {
    	return this.type;
    }
    
    public void setJobId(int jobId)
    {
    	this.jobId = jobId;
    }
    public void setContent(String content)
    {
    	this.content = content;
    }    
    public void setOwnerUid(int ownerUid)
    {
    	this.ownerUid = ownerUid;
    }    
    public void getCreateTime(Date createTime)
    {
    	this.createTime = createTime;
    }    
    public void setType(int type)
    {
    	this.type = type;
    }
}
