package com.bangya.client.BBUI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import khandroid.ext.apache.http.client.utils.URIBuilder;


import org.json.JSONArray;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.MainListAdapter;
import com.bangya.client.comm.SFtpRequest;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.comm.WebAppClient;
import com.bangya.client.location.LocationInfo;
import com.bangya.client.location.LocationResult;
import com.bangya.client.location.SimpleLocationInfo;
import com.bangya.client.model.Job;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.JobStatus;
import com.bangya.client.widget.PullToRefreshListView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeapp.bangya.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author root
 * 3 ways to get joblist
 *  1. loadmore
 *  2. pull to refresh
 *  3. onCreateView(),which happens when activity start or from other fragment (except MyTrends) back to Findjob
 */
public class FindJob extends Fragment implements InnerCommInterface{
	private String TAG = "findBB";
	private int locreason;
	private SimpleLocationInfo loc;
	public static List<JobDTO> bangbangjoblist;
	private List<JobDTO> bangbangjoblistLocal;
	private MainListAdapter listAdapter;
    private PullToRefreshListView mainListView;
    private Button footBtn;
    private Handler handler;
    private final int UPDATE_UI_WITH_NEW_DATA = 1; //更新ui
    private final int REQ_JOBS_WITH_CONDITION_CHANGED = 2; // 更新joblst
    private int page = 1; //页数
    private int cnt_lst = 0; //统计list上次的总数
    private boolean flg_IsLoadMore = false; //标记是刷新还是载入更多
    private int reqJobScopeinMiles=3000;   //请求周边job的范围,单位m
    private TextView tvSexMale, tvSexFemale,tvSexAll;
    private String choseSex = Constants.GENDER_ALL;
    private String reqJobBySex = Constants.GENDER_ALL;
    private boolean flg_IsFilter = false;   ////标记是本地过滤(0)还是向服务器请求(1S)
    private boolean   RefreshedWhenFilter = false;    //是否已经执行过刷新请求
    private CircleProgressDiaglog cpd;
    private LocationInfo myLocation;
	BaseUtil bu=null;
    private boolean isGetLocation = false;
    private boolean isNetWorkFine = true;
    // if user accept job,and back to this fragment,refresh job list
    public static boolean  bIsAcceptHappened=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
		 bu = new BaseUtil();
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.findbb, container, false);
		 BaseUtil.innerComm.addCurrentActivity(this);
        GetLocation(); // here get location,in order to get new location if it is changed!
 		 this.RefreshFindBBJob();
        this.initialListAdapter(view);
        this.mainListView.startRefresh();
        return view;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        //has to refresh here, otherwise, if we start Activity From current to showOneBB
        // then back
        super.onResume();
        if(bIsAcceptHappened){
          this.mainListView.startRefresh();
          bIsAcceptHappened =false;
        }

    }
    @Override
    public void onPause() {
    	Log.i(TAG, "onPause");
    	if(myLocation !=null){
        	myLocation.cancelListener();
    	}
        super.onPause();
    }    
    @Override
    public void onStop() {
    	Log.i(TAG, "onStop");

        super.onStop();
    }
    @Override
    public void onDestroyView() {
    	Log.i(TAG, "onDestroyView");
		BaseUtil.innerComm.delCurrentActivity(this);
        super.onDestroyView();
    }
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {// Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
    	if(HomeActivity.bisLogin == true)
    	{
            inflater.inflate(R.menu.filter, menu );
    	}else
    	{
           inflater.inflate(R.menu.login_and_register, menu );
    	}
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		Intent it;
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_filter:
	        	Log.e(TAG,"click settting in action bar in FindBB");
				FindJob.this.onRightMore();
	        	return true;
	        case R.id.action_login:
				 it =new Intent(getActivity(),Login.class);	
				startActivity(it);
				getActivity().finish();
				return true;	        
	        case R.id.action_register:
				it=new Intent(getActivity(),Register.class);	
				startActivity(it);
				getActivity().finish();
		        return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * 本函数监听更新请求，最终完成listview的更新
	 */
	private void RefreshFindBBJob(){
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  if (msg.what == UPDATE_UI_WITH_NEW_DATA){
                	  Log.i(TAG, "sizeof list:"+FindJob.bangbangjoblist.size());
                	  FindJob.this.listAdapter.notifyDataSetChanged();//通知ui有新数据
					  //notifyDataSetChanged: Notifies the attached observers that the underlying data has been changed 
					  //and any View reflecting the data set should refresh itself.
                	  FindJob.this.mainListView.onRefreshComplete();
                	  FindJob.this.mainListView.setSelection(FindJob.this.cnt_lst);
                	  FindJob.this.footBtn.setText(R.string.loader_more);
                	  FindJob.this.footBtn.setVisibility(View.VISIBLE);
                	  bu.toastShow(getActivity(),"更新成功");
                  }
                  if(msg.what == REQ_JOBS_WITH_CONDITION_CHANGED){
            			if(loc == null){
              				bu.toastShow(getActivity(), "获取位置失败，请打开网络链接，授予帮呀获取位置的权限");
              			}
                    if(!isGetLocation){
                    	  FindJob.this.footBtn.setText(R.string.getlocation);
                       FindJob.this.footBtn.setVisibility(View.VISIBLE);
                       return;
                    	}
                    	if(!isNetWorkFine){
                    		FindJob.this.footBtn.setText(R.string.retry_network);
                        FindJob.this.footBtn.setVisibility(View.VISIBLE);
                    		return;
                    	}
                    	Log.i(TAG, "ready to refresh job list because location changed");
                    	mainListView.startRefresh();
                  }
              
            }
        };
	}
    private void initialListAdapter(View view)
    {
    	//初始化footBtn(加载更多)
    	this.footBtn = new Button(getActivity());
    	this.footBtn.setTextColor(Color.parseColor(this.getString(R.color.black).toString()));

    	this.footBtn.setBackgroundResource(R.drawable.findjob_listview_footbtn);
    	if(!this.isGetLocation){
        	this.footBtn.setText(R.string.getlocation);
        	this.footBtn.setVisibility(View.VISIBLE);	
    	}else{
        	this.footBtn.setText(R.string.loader_more);
        	this.footBtn.setVisibility(View.VISIBLE);
    	}
    	
    	this.footBtn.setOnClickListener(new View.OnClickListener() {
    		//添加加载更多事件
    		@Override
        	public void onClick(View view) {
    			// the footBtn used for load more or locate, because we do not differ loadMOre or refresh here
    			// so only handle load more here, if we differ load more and refresh, we need two different handle here
    			FindJob.this.loadMore();

        	}
    	});

        //init main list view
 		//PullToRefreshListView用法:
 		//1.获取布局,2.创建listenerToRefresh,3.填充adapter,setAdpater
         this.mainListView = (PullToRefreshListView)view.findViewById(R.id.main_list_view_find); //getView()
         this.mainListView.addFooterView(this.footBtn);
         this.mainListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {/* 下拉更新监听 */
             @Override
             public void onRefresh() {
            	 		RuntimeException here = new RuntimeException("here");
			 			here.fillInStackTrace();
			 			FindJob.this.flg_IsLoadMore = false;
			 			FindJob.this.page = 1;
			 			if(FindJob.this.flg_IsFilter == true)
			 			{/* 如果执行过过滤后刷新，那么置标记让过滤的handler里再次过滤时也刷新而不再读取本地 */
			 				FindJob.this.RefreshedWhenFilter = true;
			 			}
			 			FindJob.this.RequestNearLocJobFromSvr(FindJob.this.page,JobStatus.PUBLISHED,FindJob.this.loc,FindJob.this.reqJobScopeinMiles,FindJob.this.reqJobBySex);
			 			
			 }
         });
         FindJob.bangbangjoblist = new ArrayList<JobDTO>();
 		//将listAdapter和bangbangjoblist联系起来
         this.listAdapter = new MainListAdapter(getActivity(),bangbangjoblist);
         //将adapter填充到mainlistview即PullToRefreshView
         this.mainListView.setAdapter(listAdapter);
         
         //监听listview的点击操作
         this.mainListView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
 			public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
 				Log.v(TAG, "click position"+position+"id"+id);
 				//在此进行单个job的展开，将weiboStatusList。get(position)传入
            	Intent it= new Intent(getActivity(),ShowOneBB.class);
            	it.putExtra("bbjobid", (position-1));
            	it.putExtra("showType", Constants.CALL_FROM_FIND);
            	startActivity(it);
 			}
 		});
    }
    /**
     * 加载更多
     */
    public void loadMore(){
        this.flg_IsLoadMore = true; //设置为载入更多
        this.page++;
        if(this.flg_IsFilter == true)
        {/* 如果执行过过滤后loadmore，那么置标记让过滤的handler里再次过滤时也刷新而不再读取本地 */
        	this.RefreshedWhenFilter = true;
        }
        RequestNearLocJobFromSvr(this.page,JobStatus.PUBLISHED,this.loc,this.reqJobScopeinMiles,FindJob.this.reqJobBySex);
    }
	LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(SimpleLocationInfo location){
	        //Got the location!
			if(null != cpd)
			{
				cpd.CancleProgressDialog();
				cpd = null;
			}
			SimpleLocationInfo loctmp = loc;
	    	loc = location;
	    	if(location != null){
				isGetLocation = true;
	    	}else{
	    		isGetLocation = false;
	    	}
	    	if(loctmp == null || location == null
	    	||loctmp.getLatitude() != location.getLatitude()
	    	|| loctmp.getLongitude() != location.getLongitude() ){
	    		// only when location from locationinfo.calss is changed, refresh joblist
				Log.i(TAG, "update location now");
	 	       handler.sendEmptyMessageDelayed(REQ_JOBS_WITH_CONDITION_CHANGED,0);//更新
	    	}

	    }

	};
	public void GetLocation(){		
		myLocation = new LocationInfo();
		if(false == myLocation.getLocation(this.getActivity(), locationResult))
		{
    		bu.toastShow(getActivity(),"获取位置失败，请检查定位服务是否开启，是否授予帮呀定位权限");
    		isGetLocation = false;
    		if(this.footBtn != null){
            	this.footBtn.setText(R.string.getlocation);
            	this.footBtn.setVisibility(View.VISIBLE);		
    		}
        	return;
		}
		if(null == loc)
		{
			cpd = new CircleProgressDiaglog();
			cpd.CreateProgressDiaglog(this.getActivity(),"定位中...");
		}else
		{
			if(null != cpd)
			{
				cpd.CancleProgressDialog();
				cpd = null;
			}
			Log.i(TAG, "location longitude: "+loc.getLongitude()+"latitude: "+loc.getLatitude());
		}
	}
    //更新帮帮信息
    public void updateFindBBJobList(List<JobDTO> JobList) {
        //记录上次数量
    	int iLoop = 0;
        this.cnt_lst = FindJob.bangbangjoblist.size();
        if (this.flg_IsLoadMore == false){
            //如果是刷新就重置数据，并清空list
           this.cnt_lst = 1;
        }
        FindJob.bangbangjoblist.clear();
        if(reqJobBySex.equals(Constants.GENDER_ALL))
        {
        FindJob.bangbangjoblist.addAll(JobList);//List更新，如何和listadapter联系起来更新？参考上面
        }
        else
        {
           	while(iLoop<JobList.size())
        	{
        		if(this.reqJobBySex.equals(JobList.get(iLoop).getOwnerGender()))
        		{
        			FindJob.bangbangjoblist.add(JobList.get(iLoop));
        		}
        		iLoop++;
        	}
        }
        Log.i(TAG, "add all"+FindJob.bangbangjoblist.size()+  JobList.size());
        this.handler.sendEmptyMessageDelayed(UPDATE_UI_WITH_NEW_DATA,0);//更新
    }
    /**
     * 发送消息给服务器请求job信息
     * arg:job数量,job类型...
     */
    private void RequestNearLocJobFromSvr(int page,JobStatus jobstauts,SimpleLocationInfo loc, int range, String reqJobBySex)
    {	
    	if(null == loc )
    	{
    		if( cpd ==null )
    		{// when do not get location and do not locating
    			GetLocation();
    		}else
    		{
    			//normal, locating ...
    		}
    		return;
    	}
		if(null != cpd)
		{
			cpd.CancleProgressDialog();
		}
    	Log.i(TAG, "req  BB by location,page and jobstatus:"+page+jobstauts);
    	Log.i(TAG, "req  BB by location,longtitude:"+loc.getLongitude()+"latitude:"+loc.getLatitude());

    	BaseUtil.innerComm.webappclient.ReqJobFromSvrByLocation(BaseUtil.getSelfUserInfo().getUserId(),page*Constants.JOB_NUMBER_PER_PAGE,jobstauts,loc.getLongitude(),loc.getLatitude(),range,reqJobBySex);
    }

//    public void onBackPressed() { // useless here, may homeActivity do it
 //       this.backToHomePage();
 //   }
//	private void backToHomePage()
//	{
		//销毁Activity栈中的本Activity
//		BaseUtil.queue.getLast().finish();
//		BaseUtil.bbjobArrayList.clear();
		//重新跳转到LoginActivity
	//	Intent intent=new Intent(this, HomePage.class);
	//	startActivity(intent);
//	}
	/**
	 * 按键时监听
	*/
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if(view.getId() == R.id.action_settings)
			{
			//	FindBB.this.onRightMore(view);
			}else 
			{
				FindJob.this.setSexTv(view.getId());
			}
		}
	};


	public void onRightMore()
	{
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.right_more_normal, null);
	    AlertDialog.Builder builder=  new AlertDialog.Builder(
	    		new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light));
	    
	    builder.setNegativeButton("取消", null)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {	            	
            	filterBBjobWithSex(choseSex);
            }
		});
        
	    AlertDialog dialog = builder.create(); 
	    dialog.setView(contentView);
	    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
	    lp.gravity = Gravity.TOP | Gravity.LEFT;
	    lp.x = 25; // 新位置X坐标
	    lp.y = 100; // 新位置Y坐标
	    dialog.show();
	    //published no need sex filter
	    
	    TextView tvJobstatusname = (TextView)contentView.findViewById(R.id.right_more_jobstatus_name);
	    tvJobstatusname.setVisibility(View.GONE);
	    LinearLayout linearLline0 = (LinearLayout)contentView.findViewById(R.id.right_more_seperateline0);
	    linearLline0.setVisibility(View.GONE);
	    LinearLayout linearLline1 = (LinearLayout)contentView.findViewById(R.id.right_more_jobstatus_line1);
	    linearLline1.setVisibility(View.GONE);
	    LinearLayout linearLline2 = (LinearLayout)contentView.findViewById(R.id.right_more_jobstatus_line2);
	    linearLline2.setVisibility(View.GONE);

	    tvSexAll = (TextView)contentView.findViewById(R.id.right_more_sex_all);
	    tvSexAll.setOnClickListener(listener);
	    tvSexMale = (TextView)contentView.findViewById(R.id.right_more_sex_male);
	    tvSexMale.setOnClickListener(listener);
	    tvSexFemale = (TextView)contentView.findViewById(R.id.right_more_sex_female);
	    tvSexFemale.setOnClickListener(listener);
	    if(choseSex == Constants.GENDER_ALL	)	{
	    	tvSexAll.setBackgroundResource(R.color.theme_blue);
	    }
	    if(choseSex == Constants.MALE)	{
	    	tvSexMale.setBackgroundResource(R.color.theme_blue);
	    }
	    if(choseSex == Constants.FEMALE	)	{
	    	tvSexFemale.setBackgroundResource(R.color.theme_blue);
	    }
	}
	private void setSexTv(int rID)
	{
		
		tvSexAll.setBackgroundResource(R.drawable.background_right_more_chose);
		tvSexMale.setBackgroundResource(R.drawable.background_right_more_chose);
		tvSexFemale.setBackgroundResource(R.drawable.background_right_more_chose);
		switch(rID)
		{
		case R.id.right_more_sex_female:
			tvSexFemale.setBackgroundResource(R.color.theme_blue);
			choseSex = Constants.FEMALE;
			break;
		case R.id.right_more_sex_male:
			tvSexMale.setBackgroundResource(R.color.theme_blue);
			choseSex = Constants.MALE;
			break;
		case R.id.right_more_sex_all:
			tvSexAll.setBackgroundResource(R.color.theme_blue);
			choseSex = Constants.GENDER_ALL;
			break;
		default:
			break;
		}
		
	}
	private void filterBBjobWithSex(String choseSex)
	{
		reqJobBySex = choseSex;
		if(reqJobBySex == Constants.GENDER_ALL)
		{
			flg_IsFilter = false;
		}
		else
		{
			flg_IsFilter = true;
		}
		if(this.RefreshedWhenFilter == false)
		{
			/*	如果已经过滤但是没有更新过，不向服务器再次请求，读取本地即可 */
			this.mainListView.prepareForRefresh();
			this.updateFindBBJobList(bangbangjoblistLocal);
		}
		else
		{/*  如果在过滤且更新过的前提下再次过滤，则向服务器请求 */
			this.mainListView.startRefresh();
		}
		//	RequestAcceptedJobFromSvr();  //save server pressure, process this in client
		if(reqJobBySex == Constants.GENDER_ALL)
		{
			this.RefreshedWhenFilter=false;
		}
	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		isNetWorkFine =true;
  		switch (msg.what)
		{
			case MessageId.REQUEST_FINDBB_SUCCESS_UI:
				Log.i(TAG, "recieve find bb success");
				bangbangjoblistLocal =(List<JobDTO>) msg.obj;
				bu.checkHeadPhotoUpdated(this.getActivity(),bangbangjoblistLocal,SFtpRequest.getAvatarForOwner);
				updateFindBBJobList(bangbangjoblistLocal);
				break;
			case MessageId.REQUEST_FINDBB_FAIL_UI:
				bu.toastShow(getActivity(),"获取数据失败，请稍后再试");
				break;
			case MessageId.CONNECT_SERVER_ERROR:
			{
				Log.e(TAG, "connect server error");
				bu.toastShow(getActivity(),"网络连接失败，请恢复后再试");
				isNetWorkFine = false;
		       this.handler.sendEmptyMessageDelayed(REQ_JOBS_WITH_CONDITION_CHANGED,0);//更新
				break;
			}
			default:
				Log.e(TAG, "unexpected msg :"+msg.what);
				break;
		}
	}

}
