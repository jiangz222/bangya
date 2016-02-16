package com.bangya.client.BBUI;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.bangya.client.Util.Constants;
import com.bangya.client.Util.DateTime;
import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.UILProvider;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.model.BangYaPushType;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.JobStatus;
import com.bangya.client.model.User;
import com.bangya.client.weibo.AccessTokenKeeper;
import com.igexin.sdk.PushManager;
import com.joeapp.bangya.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowOneBB  extends Activity implements InnerCommInterface ,IWeiboHandler.Response{
	private String TAG="showBB";
	private String TITLE_NAME="帮助详情";
	private int showJobId=0;
	private int showType=0;
	//Button tabRightBtn;
	private int setPoint=3;
	private TextView tv_point_desc;
	BaseUtil bu = null;
	private DisplayImageOptions options;
	private ImageView share_to_weibo_image;
    /** 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;
    private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showone_bangya);
		bu = new BaseUtil();
		BaseUtil.innerComm.addCurrentActivity(this);
		getActionBar().setTitle(TITLE_NAME); 
		this.initUIL();
		this.initUI();
		this.updatePushNotify();
	}
	@Override
	protected void onResume(){
		Log.i(TAG, "onresume");
		super.onResume();
		this.updateChatImage();
	}
	@Override
	protected void onStart(){
		Log.i(TAG, "onStart");
		super.onStart();
	}
	@Override
	protected void onPause(){
		Log.i(TAG, "onPause");
		super.onPause();
	}
	@Override
	protected void onStop(){
		Log.i(TAG, "onStop");
		super.onStop();
	}
	@Override
	protected void onDestroy(){
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	void updatePushNotify(){
		//clear publish/pick ui notify if exist
		if(Constants.CALL_FROM_PUBLISHER == showType){
			PushReceiver.PCCInfo.clearClientInfo(BangYaPushType.JOB_STATUS,Published.bangbangjoblist.get(showJobId).getJobId(),this);
		}else if(Constants.CALL_FROM_ACCEPTER == showType){
			PushReceiver.PCCInfo.clearClientInfo(BangYaPushType.JOB_STATUS,Accepted.bangbangjoblist.get(showJobId).getJobId(),this);
		}else{//what's this?
			if(HomeActivity.bisLogin == true)
			PushReceiver.PCCInfo.clearClientInfo(BangYaPushType.JOB_STATUS,showJobId,this);
		}
	}
	void initUIL()
	{
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_head)
		.showImageForEmptyUri(R.drawable.default_head)
		.showImageOnFail(R.drawable.default_head)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(Constants.CALL_FROM_PUBLISHER == showType){
			//need accepter user info 
			
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{// publisher point to accepter, set point right icon on action bar 
			   getMenuInflater().inflate(R.menu.point, menu );
			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PUBLISHED))
			{// before someone accept this job, publisher can close this job 
				getMenuInflater().inflate(R.menu.close, menu );
			}
		}else 
		if(Constants.CALL_FROM_ACCEPTER == showType){
			//需要请求发送者信息
			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{
				getMenuInflater().inflate(R.menu.point, menu );

			}
		}else
		{
			getMenuInflater().inflate(R.menu.accept, menu );

		}

		return true;
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_close:
	        	Log.e(TAG,"click settting in action bar in FindBB");
				ShowOneBB.this.showOneBBRightMoreHandler();
	        	return true;
	        case R.id.action_point:
	        	Log.e(TAG,"click settting in action bar in FindBB");
				ShowOneBB.this.showOneBBRightMoreHandler();
	        	return true;
	        case R.id.action_accept:
	        	Log.e(TAG,"click settting in action bar in FindBB");
	        	if(HomeActivity.bisLogin == true)
	        	{
	        		ShowOneBB.this.showOneBBRightMoreHandler();
	        	}else{
	        		bu.toastShow(this, "请返回后登陆,再进行此操作");
	        	}
	        		
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void initUI()
	{		
		Intent intent=getIntent();
		showJobId = (int)intent.getIntExtra("bbjobid",0);
		showType = intent.getIntExtra("showType",1);
		Log.i(TAG, "jobid and showType:"+showJobId+showType);
        TextView publisherNameText = (TextView) findViewById(R.id.showonebb_ownername_input);
        RatingBar publisherPoints = (RatingBar) findViewById(R.id.showonebb_ownerpoints);
        TextView accepterNameText = (TextView) findViewById(R.id.showonebb_accname_input);
        RatingBar accepterPoints = (RatingBar) findViewById(R.id.showonebb_accpoints);
        TextView titleText = (TextView) findViewById(R.id.showonebb_bbtitle_input);
        TextView detailText = (TextView) findViewById(R.id.showonebb_bbdetail_input);
        TextView rewardText = (TextView) findViewById(R.id.showonebb_bbreward_input);
        TextView rewardTypeText = (TextView) findViewById(R.id.showonebb_bbreward_type_input);
        TextView deadTimeTypeText = (TextView) findViewById(R.id.showonebb_bbdeadtime_input);
		TextView jobStatustv = (TextView)findViewById(R.id.showonebb_job_status_input);
        RatingBar pointToOwner = (RatingBar) findViewById(R.id.showonebb_owner_get_points);
        RatingBar pointToPicker = (RatingBar) findViewById(R.id.showonebb_picker_get_points);

        Button buttonGiveUp = (Button) findViewById(R.id.showOnebb_GiveUp);
        Button buttonFinish = (Button) findViewById(R.id.showOnebb_Finish);
		RelativeLayout pickerUserInfo = (RelativeLayout)findViewById(R.id.showonebb_accuser);
		RelativeLayout PublisherUserInfo = (RelativeLayout)findViewById(R.id.showonebb_owneruser);

		buttonGiveUp.setVisibility(View.GONE);
		buttonFinish.setVisibility(View.GONE);
        detailText.setMovementMethod(ScrollingMovementMethod.getInstance());
        ImageView ownerPhoto = (ImageView) findViewById(R.id.showonebb_ownerhead);
        ImageView pickerPhoto = (ImageView) findViewById(R.id.showonebb_acchead);
        RelativeLayout sharetosns =(RelativeLayout)findViewById(R.id.showonebb_share_sns);
        share_to_weibo_image = (ImageView) findViewById(R.id.share_to_weibo_image);
        
        ownerPhoto.setOnClickListener(listener);
        pickerPhoto.setOnClickListener(listener);
        pickerUserInfo.setOnClickListener(listener);
        PublisherUserInfo.setOnClickListener(listener);
		if(Constants.CALL_FROM_FIND == showType){
			sharetosns.setVisibility(View.GONE);
		}

			share_to_weibo_image.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// share to weibo?
					mAccessToken = AccessTokenKeeper.readAccessToken(ShowOneBB.this);
					if(false == mAccessToken.isSessionValid())
					{
						requestWBAuth();
					}else{
						//shareToweiboByStatusAPI();
						shareToWeiBoByWBClient();

					}
					return;
				
			}});
        
		if(Constants.CALL_FROM_PUBLISHER == showType){
			//need accepter user info 
			
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{// publisher point to accepter, set point right icon on action bar 
				jobStatustv.setText(R.string.pls_point_to_picker);
			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PUBLISHED))
			{// before someone accept this job, publisher can close this job 
				pickerUserInfo.setVisibility(View.GONE);
				jobStatustv.setText(R.string.just_publish);

			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PICKED))
			{// if someone accept this job, publisher can do nothing but waitting 
				jobStatustv.setText(R.string.wait_for_help);
			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.CLOSED))
			{
				if(Published.bangbangjoblist.get(showJobId).getPickerUid() == Constants.ADMIN_UID)
				{//  publisher close job himslef
					pickerUserInfo.setVisibility(View.GONE);
				}
				jobStatustv.setText(R.string.job_colsed);
			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.EXPIRED))
			{
				jobStatustv.setText(R.string.job_timeout_closed);
			}

			publisherNameText.setText(Published.bangbangjoblist.get(showJobId).getOwnerNickName());
		//	publisherPointsText.setText((new DecimalFormat("#.##")).format((float)Published.bangbangjoblist.get(showJobId).getOwnerPoints()/Published.bangbangjoblist.get(showJobId).ownerCompleteCount));
			publisherPoints.setRating((float)Published.bangbangjoblist.get(showJobId).getOwnerPoints()/Published.bangbangjoblist.get(showJobId).ownerCompleteCount);
			accepterNameText.setText(Published.bangbangjoblist.get(showJobId).getPickerNickName());
		//	accepterPointsText.setText((new DecimalFormat("#.##")).format((float)Published.bangbangjoblist.get(showJobId).getPickerPoints()/Published.bangbangjoblist.get(showJobId).pickerCompleteCount));
			accepterPoints.setRating((float)Published.bangbangjoblist.get(showJobId).getPickerPoints()/Published.bangbangjoblist.get(showJobId).pickerCompleteCount);
	        titleText.setText(Published.bangbangjoblist.get(showJobId).getTitle());
	        detailText.setText(Published.bangbangjoblist.get(showJobId).getDescription());
	        rewardTypeText.setText(Published.bangbangjoblist.get(showJobId).getRewardTypeFromIntToString(
	        getApplicationContext(), Published.bangbangjoblist.get(showJobId).getRewardType()));
	        rewardText.setText(Published.bangbangjoblist.get(showJobId).getReward());
	        deadTimeTypeText.setText(Published.bangbangjoblist.get(showJobId).getDuetimeAsString());
			//buttonComments.setOnClickListener(listener);
	        pointToOwner.setRating(Published.bangbangjoblist.get(showJobId).getPointToOwner());
	        pointToPicker.setRating(Published.bangbangjoblist.get(showJobId).getPointToPicker());
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(Published.bangbangjoblist.get(showJobId).getOwnerUid()), ownerPhoto, options);
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(Published.bangbangjoblist.get(showJobId).getPickerUid()), pickerPhoto, options);

		}
		else if(Constants.CALL_FROM_ACCEPTER == showType){
			//需要请求发送者信息
			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PICKED))
			{
				jobStatustv.setText(R.string.pls_do_help);

				buttonGiveUp.setVisibility(View.VISIBLE);
				buttonFinish.setVisibility(View.VISIBLE);
				buttonGiveUp.setOnClickListener(listener);
				buttonFinish.setOnClickListener(listener);
			}else
			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{
				jobStatustv.setText(R.string.pls_point_to_owner);
			}else
			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.CLOSED))
			{
					jobStatustv.setText(R.string.job_colsed);
			}else
			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.EXPIRED))
			{
					jobStatustv.setText(R.string.job_timeout_closed);
			}
			publisherNameText.setText(Accepted.bangbangjoblist.get(showJobId).getOwnerNickName());
			//publisherPointsText.setText((new DecimalFormat("#.##")).format((float)Accepted.bangbangjoblist.get(showJobId).getOwnerPoints()/Accepted.bangbangjoblist.get(showJobId).ownerCompleteCount));
			publisherPoints.setRating((float)Accepted.bangbangjoblist.get(showJobId).getOwnerPoints()/Accepted.bangbangjoblist.get(showJobId).ownerCompleteCount);
			accepterNameText.setText(Accepted.bangbangjoblist.get(showJobId).getPickerNickName());
		//	accepterPointsText.setText((new DecimalFormat("#.##")).format((float)Accepted.bangbangjoblist.get(showJobId).getPickerPoints()/Accepted.bangbangjoblist.get(showJobId).pickerCompleteCount));
			accepterPoints.setRating((float)Accepted.bangbangjoblist.get(showJobId).getPickerPoints()/Accepted.bangbangjoblist.get(showJobId).pickerCompleteCount);
			titleText.setText(Accepted.bangbangjoblist.get(showJobId).getTitle());
	        detailText.setText(Accepted.bangbangjoblist.get(showJobId).getDescription());
	        rewardTypeText.setText(Accepted.bangbangjoblist.get(showJobId).getRewardTypeFromIntToString(
	        		getApplicationContext(), Accepted.bangbangjoblist.get(showJobId).getRewardType()));
	        rewardText.setText(Accepted.bangbangjoblist.get(showJobId).getReward());
	        deadTimeTypeText.setText(Accepted.bangbangjoblist.get(showJobId).getDuetimeAsString());
	        pointToOwner.setRating(Accepted.bangbangjoblist.get(showJobId).getPointToOwner());
	        pointToPicker.setRating(Accepted.bangbangjoblist.get(showJobId).getPointToPicker());
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(Accepted.bangbangjoblist.get(showJobId).getOwnerUid()), ownerPhoto, options);
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(Accepted.bangbangjoblist.get(showJobId).getPickerUid()), pickerPhoto, options);

		}else 
		{		
			pickerUserInfo.setVisibility(View.GONE);
	        publisherNameText.setText(FindJob.bangbangjoblist.get(showJobId).getOwnerNickName());
	      //  publisherPointsText.setText((new DecimalFormat("#.##")).format((float)FindBB.bangbangjoblist.get(showJobId).getOwnerPoints()/FindBB.bangbangjoblist.get(showJobId).ownerCompleteCount));
		      publisherPoints.setRating((float)FindJob.bangbangjoblist.get(showJobId).getOwnerPoints()/FindJob.bangbangjoblist.get(showJobId).ownerCompleteCount);

	        titleText.setText(FindJob.bangbangjoblist.get(showJobId).getTitle());
	        detailText.setText(FindJob.bangbangjoblist.get(showJobId).getDescription());
	        rewardTypeText.setText(FindJob.bangbangjoblist.get(showJobId).getRewardTypeFromIntToString(
	        		getApplicationContext(), FindJob.bangbangjoblist.get(showJobId).getRewardType()));
	        rewardText.setText(FindJob.bangbangjoblist.get(showJobId).getReward());
	        deadTimeTypeText.setText(FindJob.bangbangjoblist.get(showJobId).getDuetimeAsString());
	        pointToOwner.setRating(FindJob.bangbangjoblist.get(showJobId).getPointToOwner());
	        pointToPicker.setRating(FindJob.bangbangjoblist.get(showJobId).getPointToPicker());
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(FindJob.bangbangjoblist.get(showJobId).getOwnerUid()), ownerPhoto, options);
	    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(FindJob.bangbangjoblist.get(showJobId).getPickerUid()), pickerPhoto, options);

		}
		
	}
	/**
	 * 按键时监听
	*/
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if(view.getId() == R.id.showOnebb_GiveUp)
			{
				new AlertDialog.Builder(ShowOneBB.this)
				.setTitle("确认放弃帮次帮助？")
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {
						//accepter give up job,update job status to published & accepter userid to null , to server
						if(ShowOneBB.this.showType != Constants.CALL_FROM_ACCEPTER)
						{
							Log.e(TAG, "impossible process here,showtype:"+ShowOneBB.this.showType);
						}else
						{
						  
						   BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(Accepted.bangbangjoblist.get(showJobId).getJobId(), 0, 0, 0, JobStatus.PICKED, JobStatus.PUBLISHED);
						}
		            }
				})
				.setNegativeButton("取消", null)
				.show();
			}else
			if(view.getId() == R.id.showOnebb_Finish)
			{
				new AlertDialog.Builder(ShowOneBB.this)
				.setTitle("确认已经完成此次帮助？")
				.setMessage("如果未完成而点击确定，会影响您的评分！")
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {
						//accepter finish job,update job status to complete to server
						BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(Accepted.bangbangjoblist.get(showJobId).getJobId(), 0, 0, 0, JobStatus.PICKED, JobStatus.COMPLETED);
		            }
				})
				.setNegativeButton("取消", null)
				.show();
			}
			else 
			if(view.getId() == R.id.showonebb_comments){
				
				Intent it=new Intent(ShowOneBB.this,ChatActivity.class);
				if(Constants.CALL_FROM_FIND == showType){
					bu.toastShow(ShowOneBB.this,"获取帮助id失败，无法留言");
				}else 		
				if(Constants.CALL_FROM_PUBLISHER == showType){
	            	it.putExtra("jobid",Published.bangbangjoblist.get(showJobId).getJobId());
					startActivity(it);
				}else
				if(Constants.CALL_FROM_ACCEPTER == showType){
	            	it.putExtra("jobid",Accepted.bangbangjoblist.get(showJobId).getJobId());
					startActivity(it);
					}

			}else if(view.getId() == R.id.showonebb_ownerhead){
				// show image in full screen
				int uid;
				if(Constants.CALL_FROM_PUBLISHER == showType){
					uid = Published.bangbangjoblist.get(showJobId).getOwnerUid();
				}else if(Constants.CALL_FROM_ACCEPTER == showType){
					uid = Accepted.bangbangjoblist.get(showJobId).getOwnerUid();
				}else if(Constants.CALL_FROM_FIND == showType){
					uid = FindJob.bangbangjoblist.get(showJobId).getOwnerUid();
				}else{
					return;
				}
				Intent it=new Intent(ShowOneBB.this,ShowImageInFullScreen.class);
            	it.putExtra("uid",uid);
				startActivity(it);
			}else if (view.getId() == R.id.showonebb_acchead){
				// show head image
				int uid;
				if(Constants.CALL_FROM_PUBLISHER == showType){
					uid = Published.bangbangjoblist.get(showJobId).getPickerUid();
				}else if(Constants.CALL_FROM_ACCEPTER == showType){
					uid = Accepted.bangbangjoblist.get(showJobId).getPickerUid();
				}else {
					return;
				}
				Intent it=new Intent(ShowOneBB.this,ShowImageInFullScreen.class);
            	it.putExtra("uid",uid);
				startActivity(it);
			}else if(view.getId() == R.id.showonebb_accuser){
				// show user info
				User user = null;
				if(Constants.CALL_FROM_PUBLISHER == showType){
					user = initUserForShowUserInfo(Published.bangbangjoblist.get(showJobId),true);
				}else if(Constants.CALL_FROM_ACCEPTER == showType){
					user = initUserForShowUserInfo(Accepted.bangbangjoblist.get(showJobId),true);
				}else {
					return;
				}
				Intent mIntent = new Intent(ShowOneBB.this,EditUserInfo.class);  
		       Bundle mBundle = new Bundle();  
		       mBundle.putSerializable("user",user);  
		       mIntent.putExtras(mBundle);
		       startActivity(mIntent);  
			}else if(view.getId() == R.id.showonebb_owneruser){
				User user = null;

				if(Constants.CALL_FROM_PUBLISHER == showType){
					user = initUserForShowUserInfo(Published.bangbangjoblist.get(showJobId),false);
				}else if(Constants.CALL_FROM_ACCEPTER == showType){
					user = initUserForShowUserInfo(Accepted.bangbangjoblist.get(showJobId),false);
				}else if(Constants.CALL_FROM_FIND == showType){
					user = initUserForShowUserInfo(FindJob.bangbangjoblist.get(showJobId),false);
				}else{
					return;
				}
				Intent mIntent = new Intent(ShowOneBB.this,EditUserInfo.class);  
			    Bundle mBundle = new Bundle();  
			    mBundle.putSerializable("user",user);  
			    mIntent.putExtras(mBundle);
			    startActivity(mIntent);  
			}
		}
	}; 
	private User initUserForShowUserInfo(JobDTO jobDTO, boolean isFromPicker){
		User user = new User();
		if(isFromPicker){
			user.setUserId(jobDTO.getPickerUid());
			user.setNickName(jobDTO.getPickerNickName());
			user.setPoints(jobDTO.getPickerPoints());
			user.setCompleteJobCount(jobDTO.getPickerCompleteJobCount());
			user.setGender(jobDTO.getPickerGender());
			user.setBirthDay(jobDTO.getPickerBirthDay());
			user.setAge(jobDTO.getPickerAge());	
		}else{
			user.setUserId(jobDTO.getOwnerUid());
			user.setNickName(jobDTO.getOwnerNickName());
			user.setPoints(jobDTO.getOwnerPoints());
			user.setCompleteJobCount(jobDTO.getOwnerCompleteJobCount());
			user.setGender(jobDTO.getOwnerGender());
			user.setBirthDay(jobDTO.getOwnerBirthDay());
			user.setAge(jobDTO.getOwnerAge());
		}

		return user;
	}
	private void showOneBBRightMoreHandler()
	{
		if(this.showType == Constants.CALL_FROM_PUBLISHER)
		{

			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{// publisher pointed to accepter 
				if(Published.bangbangjoblist.get(showJobId).getPointToPicker() !=0 ){
					bu.toastShow(this, "对不起！不能重复评分");
					return;
				}
				LayoutInflater inflater = getLayoutInflater();
	            View layout = inflater.inflate(R.layout.point_ratingbar, (ViewGroup) getCurrentFocus()); 
				new AlertDialog.Builder(this)
				.setTitle("请给这次帮助评价")
				.setView(layout)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {	            	

						//udpate jobstatus and points to server
						BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(Published.bangbangjoblist.get(showJobId).getJobId(), setPoint, Constants.POINT_TO_PICKER, 0, JobStatus.COMPLETED, JobStatus.COMPLETED);
		            }
				})
				.setNegativeButton("取消", null)
				.show();
				RatingBar rb_point = (RatingBar)layout.findViewById(R.id.point_rating_bar);
				 tv_point_desc = (TextView)layout.findViewById(R.id.point_rating_bar_desc);

				rb_point.setOnRatingBarChangeListener(new OnRatingBarChangeListener() { 

					    public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) { 
					    	if(rating == 1.0)
					    	{
					    		tv_point_desc.setText("很不满意");
					    		setPoint = 1;
					    	}
					    	if(rating == 2.0)
					    	{
					    		tv_point_desc.setText("不满意");
					    		setPoint = 2;
					    	}
					    	if(rating == 3.0)
					    	{
					    		tv_point_desc.setText("一般");
					    		setPoint = 3;
					    	}
					    	if(rating == 4.0)
					    	{
					    		tv_point_desc.setText("满意");
					    		setPoint = 4;
					    	}
					    	if(rating == 5.0)
					    	{
					    		tv_point_desc.setText("很满意");
					    		setPoint = 5;
					    	}
					    	
					    }
					});			
			}else
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PUBLISHED))
			{// before someone accept this job, publisher can close this job 
				new AlertDialog.Builder(this)
				.setTitle("确定关闭?")
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {	      
						BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(Published.bangbangjoblist.get(showJobId).getJobId(), 0, 0, 0, JobStatus.PUBLISHED, JobStatus.CLOSED);
		            }
				})
				.setNegativeButton("取消", null)
				.show();
			    //update job status to server,close
			}else
			{
				// IMPOSSIBLE process here 
				Log.e(TAG,"impossible process here");
			}
		}else
		if(this.showType == Constants.CALL_FROM_ACCEPTER)
		{

			if(Accepted.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.COMPLETED))
			{

				if(Accepted.bangbangjoblist.get(showJobId).getPointToOwner() !=0 ){
					bu.toastShow(this, "对不起！不能重复评分");
					return;
				}
				// accepter  pointed to  publisher
					LayoutInflater inflater = getLayoutInflater();
		            View layout = inflater.inflate(R.layout.point_ratingbar, (ViewGroup) getCurrentFocus()); 
					new AlertDialog.Builder(this)
					.setTitle("请给发布者评价")
					.setView(layout)
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
			            public void onClick(DialogInterface dialog, int which) {	            	
			            	BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(Accepted.bangbangjoblist.get(showJobId).getJobId(), ShowOneBB.this.setPoint, Constants.POINT_TO_OWNER, 0, JobStatus.COMPLETED, JobStatus.COMPLETED);
			            }
					})
					.setNegativeButton("取消", null)
					.show();
				RatingBar rb_point = (RatingBar)layout.findViewById(R.id.point_rating_bar);
				 tv_point_desc = (TextView)layout.findViewById(R.id.point_rating_bar_desc);
				rb_point.setOnRatingBarChangeListener(new OnRatingBarChangeListener() { 

					    public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) { 
					    	if(rating == 1.0)
					    	{
					    		tv_point_desc.setText("很不满意");
					    		setPoint = 1;
					    	}else 	if(rating == 2.0)
					    	{
					    		tv_point_desc.setText("不满意");
					    		setPoint = 2;
					    	}else  if(rating == 3.0)
					    	{
					    		tv_point_desc.setText("一般");
					    		setPoint = 3;
					    	}else if(rating == 4.0)
					    	{
					    		tv_point_desc.setText("满意");
					    		setPoint = 4;
					    	}else if(rating == 5.0)
					    	{
					    		tv_point_desc.setText("很满意");
					    		setPoint = 5;
					    	}else {
					    		tv_point_desc.setText("很不满意");
					    		setPoint = 1;
					    		ratingBar.setRating(1);
					    	}
					    	
					    }
					});			
			}
		}else
		if(this.showType == Constants.CALL_FROM_FIND)
		{//accepte this job 
			new AlertDialog.Builder(this)
			.setTitle("确定接受本求助?")
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {	      
	    			BaseUtil.innerComm.webappclient.UpdateJobStatusToSvr(FindJob.bangbangjoblist.get(showJobId).getJobId(), 0, 0, BaseUtil.getSelfUserInfo().getUserId(), JobStatus.PUBLISHED, JobStatus.PICKED);
	            }
			})
			.setNegativeButton("取消", null)
			.show();
		}

	}
	public void showOnebbBack(View view)
	{
	    //销毁Activity栈中的本Activity
		this.onBackPressed();
	}
	@Override
    public void onBackPressed() {
		Intent it = null;
		if(this.showType == Constants.CALL_FROM_FIND){
       // 	toastShow(this,"帮帮接受成功");
			// do not clear here, because when return to FindJob,We want joblist do not refresh in onresume
			//  if clear here and onresume refresh joblist get error
		//	FindJob.bangbangjoblist.clear();
			BaseUtil.innerComm.delCurrentActivity(this);
			finish();

		}else
		if(this.showType == Constants.CALL_FROM_ACCEPTER){
			Accepted.bangbangjoblist.clear();
			it= new Intent(this,Accepted.class);	
			BaseUtil.innerComm.delCurrentActivity(this);
			finish();			
			startActivity(it);

		}else
		if(this.showType == Constants.CALL_FROM_PUBLISHER){
			Published.bangbangjoblist.clear();
			it= new Intent(this,Published.class);	
			BaseUtil.innerComm.delCurrentActivity(this);
			finish();
			startActivity(it);
		}
		
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what)
		{
			case MessageId.UPDATE_BB_STATUS_SUCCESS_UI:
				Log.e(TAG, "update success");
	        	bu.toastShow(this,"帮帮更新成功");
	    		if(this.showType == Constants.CALL_FROM_FIND){
	    			Intent it = new Intent(this,Accepted.class);	
	    			startActivity(it);
	    			BaseUtil.innerComm.delCurrentActivity(this);
	    			FindJob.bIsAcceptHappened =true;
	    			finish();	    		
	    			}
	    		else
	    		{
		        	this.onBackPressed();
	    		}
	    			
	    	break;
			case MessageId.UPDATE_BB_STATUS_FAIL_UI:
	        	bu.toastShow(this,"帮帮更新失败，请稍后再试！");
	        	this.onBackPressed();
				break;
				
			case MessageId.CONNECT_SERVER_ERROR:
				bu.toastShow(this,"网络连接失败，请恢复后再试");
				break;			
			default:
				Log.e(TAG, "unkonw msg: "+msg.what);
				break;
		}
	}
	private String getShareTextInfo()
	{
		String st = null;
    	if(Constants.CALL_FROM_PUBLISHER == showType){
   		 st = "我在#帮呀#发布了求助信息，快来帮助我吧: "+Published.bangbangjoblist.get(showJobId).getTitle();
    	}else if(Constants.CALL_FROM_ACCEPTER == showType){
        st = "我又在#帮呀#帮助了别人！晒一下:"+Accepted.bangbangjoblist.get(showJobId).getTitle();
    	}
		return st;
	}
	private void requestWBAuth(){
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        // SSO 授权, 仅客户端
        Log.e(TAG, "ready to get token");

       mSsoHandler.authorizeClientSso(new AuthListener());
       
	}
	/***
	 *  share方式：第三方应用->微薄客户端->第三方应用
	 */
	private void shareToWeiBoByWBClient()
	{
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
		mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
     //[JZ]暂时不考虑
		// mWeiboShareAPI.handleWeiboResponse(this.getIntent(), this);
        
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        TextObject textObject = new TextObject();
        textObject.text = getShareTextInfo();
		weiboMessage.textObject = textObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(this, request);


	}
	/**
	 * share方式：直接调用statusAPI发送
	 */
	private void shareToweiboByStatusAPI(){
        // 获取当前已保存过的 Token
		new AlertDialog.Builder(ShowOneBB.this)
		.setTitle("确定分享本条帮助到微博?")
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {	  
            	String st;
		        mAccessToken = AccessTokenKeeper.readAccessToken(ShowOneBB.this);
		        // 对statusAPI实例化
		       mStatusesAPI = new StatusesAPI(ShowOneBB.this, Constants.APP_KEY, mAccessToken);
            mStatusesAPI.update(getShareTextInfo(), null, null, mListener);		

            }
		})
		.setNegativeButton("取消", null)
		.show();
	}
    /**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "IN ON NEW INTENT");
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        Log.e(TAG, "INTO call back of token");
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

	  /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(ShowOneBB.this, mAccessToken);
                Toast.makeText(ShowOneBB.this, 
                        "get token of weibo", Toast.LENGTH_SHORT).show();
             //   shareToweiboByStatusAPI();
                shareToWeiBoByWBClient();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                Log.e(TAG, "fail to get token");

                String code = values.getString("code");
                String message = "fail to get weibo token";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(ShowOneBB.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(ShowOneBB.this, 
                    "cancel to get weibo token", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(ShowOneBB.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(ShowOneBB.this, 
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(), 
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(ShowOneBB.this, 
                            "分享微博成功, id = " + status.id, 
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShowOneBB.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(ShowOneBB.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
    private void updateChatImage()
    {
        ImageView ivComments = (ImageView) findViewById(R.id.showonebb_comments);

		if(Constants.CALL_FROM_PUBLISHER == showType){
			if(Published.bangbangjoblist.get(showJobId).getJobStatus().equals(JobStatus.PUBLISHED))
			{
				ivComments.setVisibility(View.GONE);
			}else{
	        if(PushReceiver.PCCInfo.isMsgNotified(Published.bangbangjoblist.get(showJobId).getJobId()))
	        	{
	        		ivComments.setImageResource(R.drawable.msg_notify_large);
	        	}else
	        	{
	        		ivComments.setImageResource(R.drawable.msg_normal);
	        	}
	        	ivComments.setOnClickListener(listener);
			}
		}else if(Constants.CALL_FROM_ACCEPTER == showType){
	        if(PushReceiver.PCCInfo.isMsgNotified(Accepted.bangbangjoblist.get(showJobId).getJobId()))
	        {
	        	ivComments.setImageResource(R.drawable.msg_notify_large);
	        }else{
        		ivComments.setImageResource(R.drawable.msg_normal);
	        }
	        ivComments.setOnClickListener(listener);
		}else{
			ivComments.setVisibility(View.GONE);
		}
    }
    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
            		getString(R.string.weibosdk_demo_toast_share_failed)+"Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
	}

}
