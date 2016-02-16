package com.bangbang.webapi.server.repository;


import java.util.List;

import com.bangbang.webapi.server.model.MessagesDAO;
import com.bangbang.webapi.server.model.MessagesDTO;

/**
 * Created by wisp on 3/29/14.
 */
public interface IMsgsRepository {
    List<MessagesDTO> GetMsgsByUserId(int userId); 
    List<MessagesDTO> GetMsgsByJobId(int jobId);
    MessagesDTO GetMsgsById(int commentId);
    MessagesDTO CreateMsg(int userId, int jobId, String description); 
    MessagesDAO insertOneMsg(int jobId,int ownerUid, int type,String content);
    Boolean DeleteMsgs(int commentId);
}
