package com.bangya.client.Util;

public  interface  Constants {
	// COMMON
	public static final boolean BB_TRUE = true;
	public static final boolean BB_FALSE = false;
	/* IOClient */
	public static final int CONNECTING = 0;
	public static final int CONNECTED = 1;
	public static final String SERVER_IP="192.168.1.103";
	public static final int SERVER_PORT=8085;
	
	public static final String GENDER_ALL="a";
	public static final String MALE ="m";
	public static final String FEMALE="f";

	public static final int INVALIDE_AGE=0xffffffff;
	public static final int INVALIDE_POINTS=0xffffffff;
	public static final int INVALIDE_TIME_STAMP=0xffffffff;
	public static final int INVALIDE_BIRTH_DAY=0xffffffff;
	public static final String INVALIDE_STRING="NULL";
	public static final int INVALIDE_INT=0xffffffff;

	public static final int REWARD_TYPE_RMB=1;
	public static final int REWARD_TYPE_OBJECT=2;
	public static final int REWARD_TYPE_ZAN=3;
	public static final int REWARD_TYPE_OTHER=4;
	public static final int REWARD_TYPE_NULL=5;
	
	
	public static final int JOB_STATUS_ALL=0;                //not allow to change this value, bing to hard codes
	public static final int JOB_STATUS_PUBLISHED=1;
	public static final int JOB_STATUS_ACCEPTED=2;
	public static final int JOB_STATUS_PUB_POINT=4;     //publihser point to accepter,real job status
	public static final int JOB_STATUS_ACC_POINT=8;    //accpeter point to publisher,real job status
	public static final int JOB_STATUS_CLOSED=16;

	/*  include both pub-point and close point status ,when user request job and filter job,only use this value */
	public static final int JOB_STATUS_POINTING=12;     
	public static final int JOB_STATUS_INVALIED=0xff;
	
	public static final int CALL_FROM_PUBLISHER=1;
	public static final int CALL_FROM_ACCEPTER=2;
	public static final int CALL_FROM_FIND=3;

	public static int JOB_NUMBER_PER_PAGE=8;
	
	public static final int GET_LAST_LOC_NULL = 1;
	public static final int PROVIDER_DISENABLE = 2;
	
	public static final int POINT_TO_PICKER = 1;
	public static final int POINT_TO_OWNER = 2;
	public static final int POINT_BOTH = 3;
	
	public static final int MAX_NUMBER_MSGS_PER_USER = 8;
	
	public static final int PUSH_MSG_DIRECTION_TO_NULL = 0;
	public static final int PUSH_MSG_DIRECTION_TO_OWNER = 1;
	public static final int PUSH_MSG_DIRECTION_TO_PICKER = 2;
	public static final int PUSH_MSG_DIRECTION_TO_BID = PUSH_MSG_DIRECTION_TO_PICKER|PUSH_MSG_DIRECTION_TO_OWNER;
	public static final int ADMIN_UID=1;
	
	
	/**
	 * weibo 
	 */
    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    public static final String APP_KEY      = "VALUE_NEED_FILL";

    /** 
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
}
