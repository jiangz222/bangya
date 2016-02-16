package com.bangya.client.model;

import java.util.Date;

/**
 * Created by wisp on 3/29/14.
 */
public class Messages {
    public int msgId;
    public int jobId;
    public int ownerUid;
    public String content;
    public Date createTime;
    public int type;		//消息类型：纯文本、表情+文本、图片、语音
    
}
