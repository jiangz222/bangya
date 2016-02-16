package com.bangya.client.Util;

public interface MessageId {

	/**
	 * client to server
	 */
	public static final int REQUEST_LOGIN=0;
	public static final int REQUEST_REGISTER=REQUEST_LOGIN+1;
	public static final int REQUEST_USERINFO=REQUEST_REGISTER+1;
	public static final int UPDATE_USERINFO=REQUEST_USERINFO+1;
	public static final int CREATE_NEW_BB=UPDATE_USERINFO+1;
	public static final int REQUEST_ACCEPT_BB=CREATE_NEW_BB+1;
	 //req published job from sever, need job status and the accepter user info if it is accpeted!!
	public static final int REQUEST_PUBLISHED_BB=REQUEST_ACCEPT_BB+1;
	//req my accepted job from server, need publisher user info as well 
	public static final int REQUEST_ACCEPTED_BB=REQUEST_PUBLISHED_BB+1;
	//req near job from server , need publisher user info as well 
	public static final int REQUEST_FIND_BB = REQUEST_ACCEPTED_BB+1;
	public static final int ACCEPTED_JOB_BB = REQUEST_FIND_BB+1;
    //update publisher points and job status to acc_point, only need return success or fail
	public static final int JOB_TO_ACC_POINT = ACCEPTED_JOB_BB+1;
    //delete job by jobid,nothing else
	public static final int JOB_TO_CLOSE = JOB_TO_ACC_POINT+1;
    //give up job,clear accepter uid in job table by jobid
	public static final int JOB_TO_GIVEUP = JOB_TO_CLOSE+1;
	//only need to update job status by jobid
	public static final int JOB_TO_PUB_POINT = JOB_TO_GIVEUP+1;

	
	/**
	 * server to client
	 */
	//login
	public static final int LOGIN_RTN=2000;
	public static final int LOGIN_SUCCESS=LOGIN_RTN+1;
	public static final int LOGIN_FAIL=LOGIN_SUCCESS+1;
	//register 
	public static final int REGISTER_RTN=LOGIN_FAIL+1;
	public static final int REGISTER_SUCCESS=REGISTER_RTN+1;
	public static final int REGISTER_FAIL=REGISTER_SUCCESS+1;
	public static final int REGISTER_FAIL_EMAIL_CONFLICT=REGISTER_FAIL+1;
	public static final int REGISTER_FAIL_USERNAME_CONFLICT=REGISTER_FAIL_EMAIL_CONFLICT+1;
	
	//update userinfo
	public static final int UPDATE_USERINFO_RTN = REGISTER_FAIL_USERNAME_CONFLICT+1;
	public static final int UPDATE_USERINFO_SUCCESS = UPDATE_USERINFO_RTN+1;
	public static final int UPDATE_USERINFO_FAIL = UPDATE_USERINFO_SUCCESS+1;

	//new BB
	public static final int NEW_BB_RTN = UPDATE_USERINFO_FAIL+1;
	public static final int NEW_BB_SUCCESS=NEW_BB_RTN+1;
	public static final int NEW_BB_FAIL=NEW_BB_SUCCESS+1;
	//ACCEPT BB
	public static final int REQUEST_ACCEPTBB_RTN = NEW_BB_FAIL+1;
	public static final int REQUEST_ACCEPTBB_SUCCESS=REQUEST_ACCEPTBB_RTN+1;
	public static final int REQUEST_ACCEPTBB_FAIL=REQUEST_ACCEPTBB_SUCCESS+1;
	//SEND BB
	public static final int REQUEST_PUBLISHEDBB_RTN = REQUEST_ACCEPTBB_FAIL+1;
	public static final int REQUEST_PUBLISHEDBB_SUCCESS=REQUEST_PUBLISHEDBB_RTN+1;
	public static final int REQUEST_PUBLISHEDBB_FAIL=REQUEST_PUBLISHEDBB_SUCCESS+1;
	//FIND BB
	public static final int REQUEST_FINDBB_RTN = REQUEST_PUBLISHEDBB_FAIL+1;
	public static final int REQUEST_FINDBB_SUCCESS=REQUEST_FINDBB_RTN+1;
	public static final int REQUEST_FINDBB_FAIL=REQUEST_FINDBB_SUCCESS+1;
	//accept BB  return
	public static final int UPDATE_BB_STATUS_RTN = REQUEST_FINDBB_FAIL+1;
	public static final int UPDATE_BB_STATUS_SUCCESS=UPDATE_BB_STATUS_RTN+1;
	public static final int UPDATE_BB_STATUS_FAIL=UPDATE_BB_STATUS_SUCCESS+1;
	//JOB_TO_ACC_POINT return
	public static final int JOB_TO_ACC_POINT_RTN = UPDATE_BB_STATUS_FAIL+1;
	public static final int JOB_TO_ACC_POINT_SUCCESS=JOB_TO_ACC_POINT_RTN+1;
	public static final int JOB_TO_ACC_POINT_FAIL=JOB_TO_ACC_POINT_SUCCESS+1;
	//JOB_TO_CLOSE return
	public static final int JOB_TO_CLOSE_RTN = JOB_TO_ACC_POINT_FAIL+1;
	public static final int JOB_TO_CLOSE_SUCCESS=JOB_TO_CLOSE_RTN+1;
	public static final int JOB_TO_CLOSE_FAIL=JOB_TO_CLOSE_SUCCESS+1;
	//IO to UI
	public static final int LOGIN_SUCCESS_UI=5000;
	public static final int LOGIN_FAIL_UI=LOGIN_SUCCESS_UI+1;
	public static final int CONNECT_SERVER_ERROR=LOGIN_FAIL_UI+1;
	public static final int REGISTER_SUCCESS_UI=CONNECT_SERVER_ERROR+1;
	public static final int REGISTER_FAIL_UI=REGISTER_SUCCESS_UI+1;
	public static final int REGISTER_FAIL_EMAIL_CONFLICT_UI=REGISTER_FAIL_UI+1;
	public static final int REGISTER_FAIL_USERNAME_CONFLICT_UI=REGISTER_FAIL_EMAIL_CONFLICT_UI+1;
	public static final int UPDATE_USERINFO_SUCCESS_UI=REGISTER_FAIL_USERNAME_CONFLICT_UI+1;
	public static final int UPDATE_USERINFO_FAIL_UI=UPDATE_USERINFO_SUCCESS_UI+1;
	public static final int NEW_BB_SUCCESS_UI=UPDATE_USERINFO_FAIL_UI+1;
	public static final int NEW_BB_FAIL_UI=NEW_BB_SUCCESS_UI+1;
	public static final int REQUEST_PUBLISHED_SUCCESS_UI=NEW_BB_FAIL_UI+1;
	public static final int REQUEST_PUBLISHED_FAIL_UI=REQUEST_PUBLISHED_SUCCESS_UI+1;
	public static final int REQUEST_ACCEPTED_SUCCESS_UI=REQUEST_PUBLISHED_FAIL_UI+1;
	public static final int REQUEST_ACCEPTED_FAIL_UI=REQUEST_ACCEPTED_SUCCESS_UI+1;
	public static final int REQUEST_FINDBB_SUCCESS_UI=REQUEST_ACCEPTED_FAIL_UI+1;
	public static final int REQUEST_FINDBB_FAIL_UI=REQUEST_FINDBB_SUCCESS_UI+1;
	public static final int UPDATE_BB_STATUS_SUCCESS_UI=REQUEST_FINDBB_FAIL_UI+1;
	public static final int UPDATE_BB_STATUS_FAIL_UI=UPDATE_BB_STATUS_SUCCESS_UI+1;
	public static final int GET_USER_BY_NAME_SUCCESS_UI = UPDATE_BB_STATUS_FAIL_UI+1;
	public static final int GET_USER_BY_NAME_FAIL_UI = GET_USER_BY_NAME_SUCCESS_UI+1;
	public static final int SEND_SUGGESTION_SUCCESS_UI = GET_USER_BY_NAME_FAIL_UI+1;
	public static final int SEND_SUGGESTION_FAIL_UI = SEND_SUGGESTION_SUCCESS_UI+1;
	public static final int GET_MSGS_SUCCESS_UI = SEND_SUGGESTION_FAIL_UI+1;
	public static final int GET_MSGS_FAIL_UI = GET_MSGS_SUCCESS_UI+1;
	public static final int SEND_MSG_SUCCESS_UI = GET_MSGS_FAIL_UI+1;
	public static final int SEND_MSG_FAIL_UI = SEND_MSG_SUCCESS_UI+1;
	public static final int UPDATE_CLIENT_ID_FAIL = SEND_MSG_FAIL_UI+1;
	
	public static final int PUSH_CHAT_MSG = UPDATE_CLIENT_ID_FAIL+1;
	public static final int PUSH_JOB_STATUS = PUSH_CHAT_MSG+1;
	public static final int RESET_PASSWORD_FAIL = PUSH_JOB_STATUS+1;
	public static final int IDENTIFY_CODE_MISMATCH = RESET_PASSWORD_FAIL+1;
	public static final int EMAIL_NOT_EXIST = IDENTIFY_CODE_MISMATCH+1;
	public static final int RESET_PASSWORD_SUCCESS = EMAIL_NOT_EXIST+1;
	public static final int REQ_IDENTIFY_CODE_SUCCESS = RESET_PASSWORD_SUCCESS+1;
	public static final int REQ_IDENTIFY_CODE_FAIL = REQ_IDENTIFY_CODE_SUCCESS+1;
	public static final int CHANGE_PASSWORD_FAIL = REQ_IDENTIFY_CODE_FAIL+1;
	public static final int CHANGE_PASSWORD_SUCCESS = CHANGE_PASSWORD_FAIL+1;
	public static final int CHANGE_PASSWORD_OLD_MISMATCH = CHANGE_PASSWORD_SUCCESS+1;
	public static final int GET_SYSTEM_INFO_SUCCESS = CHANGE_PASSWORD_OLD_MISMATCH +1;
	public static final int GET_SYSTEM_INFO_FAIL = GET_SYSTEM_INFO_SUCCESS+1;
	public static final int APK_DOWN_LOAD_DONE = GET_SYSTEM_INFO_FAIL+1;

	public static final int INVALIDE_MSG_ID=0xffffffff;

}
