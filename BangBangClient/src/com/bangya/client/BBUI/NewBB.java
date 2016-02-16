package com.bangya.client.BBUI;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import khandroid.ext.apache.http.client.utils.URIBuilder;



import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.bangya.client.Util.Constants;
import com.bangya.client.Util.DateTime;
import com.bangya.client.Util.MessageId;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.location.LocationInfo;
import com.bangya.client.location.LocationResult;
import com.bangya.client.location.SimpleLocationInfo;
import com.bangya.client.model.Job;
import com.bangya.client.model.RewardType;
import com.bangya.client.weibo.AccessTokenKeeper;
import com.joeapp.bangya.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewBB extends Fragment implements InnerCommInterface{
	private final String TAG="NewBB";
	private EditText newbbTitle;
	private EditText newbbDetail;
	private Button deadTimeDate;
	private Button deadTimeHr;
	private Spinner newbbreward;
	private EditText rewardInput;
	private RewardType rewardType = RewardType.RMB;
	private int newyear,newmonth,newday,newhr,newmin;
	private Button btnConfirm;
	private Button btnCancel;
	public Toast gToast = null;
	private CircleProgressDiaglog cpd = new CircleProgressDiaglog();;
	private CheckBox shareToWEIBO;
	BaseUtil bu = null;
    private LocationInfo myLocation;
    private SimpleLocationInfo loc;
   private final int GET_LOCATION_FAIL = 1;
   private final int CREATE_NEW_BB = 2;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);
		 bu = new BaseUtil();
       setHasOptionsMenu(true);
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_bangya, container, false);
        Log.i(TAG, "onCreateView");
		BaseUtil.innerComm.addCurrentActivity(this);
		 this.processHanlder();
		this.initUI(view);

       btnConfirm = (Button)view.findViewById(R.id.newbb_confirm);
       btnCancel = (Button)view.findViewById(R.id.newbb_clean);
       btnConfirm.setOnClickListener(listener);
       btnCancel.setOnClickListener(listener);
       return view;
    }
    @Override
    public void onStart()
    {//if start another activity,and go back,onStart called
    	super.onStart();

    }
    @Override
    public void onPause(){
    	super.onPause();
    	if(null != myLocation){
    		myLocation.cancelListener();
    	}
    }
    @Override
    public void onDestroyView() {
    	Log.i(TAG, "onDestroyView");
		BaseUtil.innerComm.delCurrentActivity(this);
       super.onDestroyView();
    }
	@Override
	public void onDestroy() {
		Log.i(TAG, "newbb onDestroy");
		super.onDestroy();
	}
	@SuppressWarnings("deprecation")
	public void initUI(View view)
	{
		
		newbbTitle = (EditText)view.findViewById(R.id.newbb_title_edit);
		newbbDetail = (EditText)view.findViewById(R.id.newbb_detail_input);
		deadTimeDate = (Button)view.findViewById(R.id.deadtimedate);
		deadTimeHr = (Button)view.findViewById(R.id.deadtimehr);
		newbbreward = (Spinner)view.findViewById(R.id.newbb_reward_spinner);  
		rewardInput = (EditText)view.findViewById(R.id.rewardedit);
		cleanView();
		newbbreward.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				Log.i("NewBB","position; "+position); 
				//This callback is invoked only when the newly selected position is different 
				//from the previously selected position or if there was no selected item.
				if(0 == position)
				{
					rewardInput.setVisibility(View.VISIBLE);
					rewardInput.setHint("请输入金额");
					rewardInput.setInputType(InputType.TYPE_CLASS_NUMBER);
					rewardType = RewardType.RMB;
				}
				if(1 == position)
				{
					rewardInput.setVisibility(View.VISIBLE);
					rewardInput.setHint("请输入物品描述");
					rewardInput.setInputType(InputType.TYPE_CLASS_TEXT);
					rewardType = RewardType.OBJECT;

				}
				if(2 == position)
				{
					rewardInput.setVisibility(View.VISIBLE);
					rewardInput.setHint("也许社交账号@Ta是个好办法");
					rewardInput.setInputType(InputType.TYPE_CLASS_TEXT);
					rewardType = RewardType.ZAN;

				}
				if(3 == position)
				{
					rewardInput.setVisibility(View.VISIBLE);
					rewardInput.setHint("请随意,字数不得超过15字");
					rewardInput.setInputType(InputType.TYPE_CLASS_TEXT);
					rewardType= RewardType.OTHERS;

				}
				if(4 == position)
				{
					rewardInput.setVisibility(View.GONE);
					rewardType= RewardType.NOREWARD;

				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
	
			}
		});
		initDateTimeButton();

		
	}
	public void initDateTimeButton()
	{
		Calendar c = Calendar.getInstance();
		deadTimeDate.setText(c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+(c.get(Calendar.DAY_OF_MONTH)+3));
		deadTimeHr.setText(c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
		newyear = c.get(Calendar.YEAR);
		newmonth = c.get(Calendar.MONTH)+1;
		newday = c.get(Calendar.DAY_OF_MONTH)+3;
		newhr = c.get(Calendar.HOUR_OF_DAY);
		newmin = c.get(Calendar.MINUTE);
		deadTimeDate.setOnClickListener(listener);
		deadTimeHr.setOnClickListener(listener);
	}
	public void setDeadDate()
	{		
		final Calendar c = Calendar.getInstance();
		
		// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
		new DatePickerDialog(getActivity(),
			// 绑定监听器
			new DatePickerDialog.OnDateSetListener()
			{
				@Override
				public void onDateSet(DatePicker dp, int year,
					int month, int dayOfMonth)
				{
					/*
					if(year < c.get(Calendar.YEAR)
					   ||(year == c.get(Calendar.YEAR)&& month<c.get(Calendar.MONTH))
					   ||(year == c.get(Calendar.YEAR)&&month==c.get(Calendar.MONTH)&&dayOfMonth<c.get(Calendar.DAY_OF_MONTH)))
					
					if(DateTime.dateToInt(year, dayOfMonth, dayOfMonth) < 
						DateTime.dateToInt(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)))
					{
						datesetOK=false;
					}
					else
					{
						datesetOK=true;
					}*/
					deadTimeDate.setText(""+year+"-"+(month+1)+"-"+dayOfMonth);					
					newyear = year;
					newmonth = month+1;
					newday = dayOfMonth;
				}
			}
		//设置初始日期
		, c.get(Calendar.YEAR)
		, c.get(Calendar.MONTH)
		, c.get(Calendar.DAY_OF_MONTH)+3).show();
	}
	public void setDeadHR()
	{		
		final Calendar c = Calendar.getInstance();
		
		  new TimePickerDialog(getActivity(),new OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker view,int hourOfDay,int minute)
             {
            	  /*
            	  if(timesetOK == false)
            	  {
            		  
            	  }
                  if(
            	     newyear == c.get(Calendar.YEAR)&&newmonth==c.get(Calendar.MONTH)&&newday==c.get(Calendar.DAY_OF_MONTH))
            	  {
            		  if((hourOfDay<c.get(Calendar.HOUR_OF_DAY))
            		      ||(hourOfDay == c.get(Calendar.HOUR_OF_DAY)&&minute<c.get(Calendar.MINUTE)))
            		  {
                		timesetOK=false;
            		  }
            		  else{
                  		  timesetOK=true;
            		  }
            	  }
            	  else
            	  {
              		  timesetOK=true;
            	  }*/
            	  newhr = hourOfDay;
            	  newmin = minute;
          		  deadTimeHr.setText(hourOfDay+":"+minute);

             }                

          },c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
	}
	public void newbbConcel()
	{	    
    //    this.backToHomePage();
	}
	public void newbbConfirm()
	{
		//get newest location every time we create job!
		getLocation();
		//create new job when location update finished!
	}
	
	public boolean createNewBangya(){
		String rewardDetail;		
		
		cpd.CancleProgressDialog();
		cpd.CreateProgressDiaglog(this.getActivity(),"求助创建中，请稍后...");
		Log.i(TAG, "location longitude: "+loc.getLongitude()+"latitude: "+loc.getLatitude());
		
		String bbTitle = newbbTitle.getText().toString().trim();
		String bbDetail = newbbDetail.getText().toString().trim();
		if(bbTitle.length()>20)
		{        	
			toastShow("标题不能多于15字");
			Log.e(TAG, "title length over length");
			return false;
		}
		if(bbTitle.length() == 0)
		{
			toastShow("标题不能为空");
			Log.e(TAG, "title length over length");
			return false;
		}
		if(bbDetail.length()>150)
		{        	
			toastShow("描述不能多于150字");
			Log.e(TAG, "title length over length");
			return false;
		}
		if(bbDetail.length()==0)
		{        	
			toastShow("描述不能为空");
			Log.e(TAG, "title length over length");
			return false;
		}
		if((RewardType.RMB.getValue() <= rewardType.getValue()) 
				&& (rewardType.getValue() < RewardType.NOREWARD.getValue()))
			{
				rewardDetail = rewardInput.getText().toString().trim();
				if(rewardDetail.length() == 0)
				{
					toastShow("奖励内容不能为空");
					return false;
				}
			}
			else if(rewardType.getValue() == RewardType.NOREWARD.getValue())
			{
				rewardDetail = "";
			}
			else
			{
	        	toastShow("请输入奖励内容");
				Log.e(TAG, "pls input reward info");
				return false;
			}
		
		String sdueTime = newyear+"-"+newmonth+"-"+newday+" "+newhr+":"+newmin+":00";
	//	DeadTimeMills = DateTime.dateTimeToMillsfrom1970(newyear, newmonth, newday,newhr,newmin);
		//Time t=new Time();
	//	t.setToNow(); // 取得系统时间
		//nowMills = DateTime.dateTimeToMillsfrom1970(t.year,t.month,t.monthDay,t.hour,t.minute);
		//if(DeadTimeMills <= nowMills)
		Date dueDate = null;
		try {
			dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdueTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			toastShow("获取时间失败");
			return false;
		}
		Date currentDate = new Date();
		if(dueDate.before(currentDate) || dueDate.equals(currentDate))
		{        	
			toastShow("截止时间必须大于当前时间");
			Log.e(TAG, "tdeadtime invalid");
			return false;
		}

		BaseUtil.innerComm.webappclient.createNewBBToSvr(BaseUtil.getSelfUserInfo().getUserId(),loc.getLongitude(),loc.getLatitude(),bbTitle,bbDetail,rewardType,rewardDetail,sdueTime);
		return true;
	}
	public void cleanView()
	{
		newbbTitle.setText("");
		newbbDetail.setText("");
		initDateTimeButton();
		newbbreward.setSelection(0);
		rewardInput.setText("");;
	}
	private void processHanlder(){
	 this.handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
              if(msg.what == GET_LOCATION_FAIL){
          				bu.toastShow(getActivity(), "获取位置信息失败，无法新建求助");
          				cpd.CancleProgressDialog();
              }
              if(msg.what == CREATE_NEW_BB){
      	    	if(false == createNewBangya()){
      	    		cpd.CancleProgressDialog();
      	    	}
      	    	
              }
        }
    };
	}
	LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(SimpleLocationInfo location){
	        //Got the location!
			if(location == null){
				handler.sendEmptyMessage(GET_LOCATION_FAIL);	
			}
	    	loc = location;
	    	if(loc != null){
				handler.sendEmptyMessage(CREATE_NEW_BB);	
	    	}
	    }
	};
	public void getLocation(){
		// loc = HomeActivity.locationinfo.getLocation();
		
		myLocation = new LocationInfo();
		if(false == myLocation.getLocation(this.getActivity(), locationResult))
		{// no reason happen here
    		bu.toastShow(getActivity(),"获取位置失败，请检查设置中定位服务是否开启，是否授予帮呀定位权限");
    		cpd.CancleProgressDialog();
    		return;
		}
		cpd.CreateProgressDiaglog(this.getActivity(),"定位中，请稍后...");
		
	}
	/**
	 * 按键时监听
	*/
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if(view.getId() == R.id.newbb_confirm)
			{

				NewBB.this.newbbConfirm();
				
			}else if(view.getId() == R.id.newbb_clean)
			{	
				NewBB.this.cleanView();	

			}else if(view.getId() == R.id.deadtimedate){
				setDeadDate();
			}else if(view.getId() == R.id.deadtimehr){
				setDeadHR();
			}else
			{
				Log.e(TAG, "no chance");

			}
		}
	};

	public void toastShow(String toastString)
	{
		if(gToast == null)
		{
			gToast  = Toast.makeText(getActivity(),toastString,Toast.LENGTH_LONG);
		}else
		{
			gToast.setText(toastString);
		}
		gToast.show();

	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		cpd.CancleProgressDialog();

		switch(msg.what){
		case MessageId.NEW_BB_SUCCESS_UI:
        	bu.toastShow(this.getActivity(),"新建成功");
			NewBB.this.cleanView();	
			break;
		case MessageId.NEW_BB_FAIL_UI:
			toastShow("对不起，新建求助失败，请稍后再试");
			break;
		case MessageId.CONNECT_SERVER_ERROR:
			toastShow("网络连接失败，请恢复后再试");
			break;
		default:	
			break;
		}
	}




}
