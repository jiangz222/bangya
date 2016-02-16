package com.bangbang.webapi.server.repository;

import com.bangbang.webapi.server.model.BangYaPushType;
import com.bangbang.webapi.server.model.JobStatus;
import com.bangbang.webapi.server.model.MessagesDAO;
import com.bangbang.webapi.server.model.MessagesDTO;
import com.bangbang.webapi.server.model.User;
import com.bangbang.webapi.server.pushmanager.IPushManager;
import com.bangbang.webapi.server.pushmanager.PushManager;
import com.bangbang.webapi.server.util.BYConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.codehaus.jackson.map.util.JSONPObject;


public class MsgsRepository implements IMsgsRepository {
 	DBRepositoryOperator dbOp = new DBRepositoryOperator();
    IPushManager pm = new PushManager();

    @Override
    public List<MessagesDTO> GetMsgsByJobId(int jobId) {
		List<MessagesDTO> MsgList = new ArrayList<MessagesDTO>();
   	 	
		try {
			MsgList = dbOp.queryMsgsByJobId(jobId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MsgList = null;
		}
    	return MsgList;    
    }

	@Override
	public List<MessagesDTO> GetMsgsByUserId(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagesDTO GetMsgsById(int commentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagesDTO CreateMsg(int userId, int jobId, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean DeleteMsgs(int commentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessagesDAO insertOneMsg(int jobId, int ownerUid, int type, String content) {
		// TODO Auto-generated method stub
        // TODO Auto-generated method stub
   	 	Object newMsgid = null;
   	 	int pushToUid = 0;
   	 	int direction = 0;
   	 	MessagesDAO msg = new MessagesDAO();
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	map.put("jobid", jobId);
    	map.put("ownerUid", ownerUid);
    	map.put("content", content);
    	map.put("createTime", new Date());
    	map.put("type", type);

		try {
			newMsgid = (long)dbOp.insertOnelineDB(map, "messages");
			msg = (MessagesDAO)dbOp.queryOneObjectbySingleCondition("msgId",newMsgid,"messages",msg);
			if(msg != null){
				//notify the other user of job the new messages
				//msg restore successful, push msg to other user related to this job
				Integer jobOwnerUid = (Integer)dbOp.queryColumnValueByName(jobId, "jobId", "ownerId", "Job");
				if(jobOwnerUid == null){
					System.out.println("get jobOwnerUid fail by jobId:"+jobId);
					return msg;
				}
				if(ownerUid != jobOwnerUid.intValue()){
					//if owner of msg is not the owner of job(msg owner is the picker of job), so send the msg to owner of job
					pushToUid = jobOwnerUid.intValue();
					direction = BYConstants.PUSH_MSG_DIRECTION_TO_OWNER;
				}else{
					//msg owner is the publisher of job, so send msg to picker of job
					Integer jobPickerUid = (Integer)dbOp.queryColumnValueByName(jobId, "jobId", "pickerId", "Job");
					if(jobPickerUid == null){
						System.out.println("get jobPickerUid fail by jobId:"+jobId);
						return msg;
					}
					if(jobPickerUid.intValue() == ownerUid){
						System.out.println("impossible,jobpicker uid is equal with msg owner uid:"+jobId+jobPickerUid);
						return msg; 
					}
					pushToUid = jobPickerUid.intValue();
					direction = BYConstants.PUSH_MSG_DIRECTION_TO_PICKER;

				}
				//get push clientid of target user
				String clientId = (String)dbOp.queryColumnValueByName(pushToUid, "uid", "cid", "uidcidmap");
	
				if(null == clientId)
				{
					System.out.println("get client id fail by uid:"+pushToUid);
				}else{
					MessagesDTO msgDTO = MessagesDTO.ConvertFromDBObject(msg);
					System.out.println("get client id success by uid,now push to Uid:"+pushToUid);

					pm.pushTransmissionMsgToSingle(clientId,
					    pm.constructPushMessage(ownerUid,BangYaPushType.CHAT_MSG,jobId,direction,msgDTO));
				}
				return msg;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
