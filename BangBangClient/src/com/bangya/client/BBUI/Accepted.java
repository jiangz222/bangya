package com.bangya.client.BBUI;

import java.util.ArrayList;
import java.util.List;


import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.MainListAdapter;
import com.bangya.client.comm.SFtpRequest;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.model.Job;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.JobStatus;
import com.bangya.client.widget.PullToRefreshListView;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Accepted extends Activity implements InnerCommInterface{
	private String TAG="Accepted";
	private String TITLE_NAME="我帮助的";
	public static List<JobDTO> bangbangjoblist;
	private List<JobDTO> bangbangjoblistLocal;
	private MainListAdapter listAdapter;
    private PullToRefreshListView mainListView;
    private Button footBtn;
    private Handler handler;
    private final int UPDATE_UI = 1; //更新ui
    private int page = 1; //页数
    private int cnt_lst = 0; //统计list上次的总数
    private boolean flg_IsLoadMore = false; //标记是刷新还是载入更多
    private boolean flg_IsFilter = false;   //标记是本地过滤(0)还是向服务器请求(1S)
    private JobStatus reqJobStauts = JobStatus.ALL;
    private boolean   RefreshedWhenFilter = false;    //是否已经执行过刷新请求
    private JobStatus choseJobStatus = JobStatus.ALL;
    private TextView tvJobAll,tvJobPub,tvJobAcc,tvJobComp,tvJobCls,tvJobExp;
    BaseUtil bu = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.accepted);
		 bu = new BaseUtil();

		BaseUtil.innerComm.addCurrentActivity(this);

		this.RefreshAcceptedJob();
        this.initialListAdapter();
        this.mainListView.startRefresh();//第一次刷新
		getActionBar().setTitle(TITLE_NAME);   

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
      getMenuInflater().inflate(R.menu.filter, menu );

		return true;
	}
	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_filter:
	        	Log.e(TAG,"click settting in action bar in FindBB");
				Accepted.this.onRightMore();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	/**
	 * 本函数最终完成listview的更新
	 */
	private void RefreshAcceptedJob(){
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  if (msg.what == UPDATE_UI){
                	  Accepted.this.listAdapter.notifyDataSetChanged();//通知ui有新数据
					  //notifyDataSetChanged: Notifies the attached observers that the underlying data has been changed 
					  //and any View reflecting the data set should refresh itself.
                	  Accepted.this.mainListView.onRefreshComplete();
                	  Accepted.this.mainListView.setSelection(Accepted.this.cnt_lst);
                	  Accepted.this.footBtn.setVisibility(View.VISIBLE);
        //        	  toastShow(Accepted.this,"更新成功");
                  }
            }
        };
	}
	private void initialListAdapter()
    {
    	//初始化footBtn(加载更多)
    	this.footBtn = new Button(getApplicationContext());
    	this.footBtn.setText(R.string.loader_more);
    	this.footBtn.setTextColor(Color.parseColor(this.getString(R.color.black).toString()));
    	this.footBtn.setBackgroundColor(Color.parseColor(this.getString(R.color.background_gray).toString()));
    	this.footBtn.setVisibility(View.GONE);
    	this.footBtn.setOnClickListener(new View.OnClickListener() {
    		//添加加载更多事件
    		@Override
        	public void onClick(View view) {
    			Accepted.this.loadMore();
        	}
    	});
        //init main list view
 		//PullToRefreshListView用法:
 		//1.获取布局,2.创建listenerToRefresh,3.填充adapter,setAdpater
         this.mainListView = (PullToRefreshListView) findViewById(R.id.main_list_view_acc);
         this.mainListView.addFooterView(this.footBtn);
         this.mainListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {/* 下拉更新监听 */
             @Override
             public void onRefresh() {
			 RuntimeException here = new RuntimeException("here");
			 			here.fillInStackTrace();
			 			Accepted.this.flg_IsLoadMore = false;
			 			Accepted.this.page = 1;
			 			if(Accepted.this.flg_IsFilter == true)
			 			{/* 如果执行过过滤后刷新，那么置标记让过滤的handler里再次过滤时也刷新而不再读取本地 */
			 				Accepted.this.RefreshedWhenFilter = true;
			 			}
			 			Accepted.this.RequestAcceptedJobFromSvr(Accepted.this.page,Accepted.this.reqJobStauts);
			 }
         });
         Accepted.bangbangjoblist = new ArrayList<JobDTO>();
 		//将listAdapter和bangbangjoblist联系起来
         this.listAdapter = new MainListAdapter(getApplicationContext(),bangbangjoblist);
         //将adapter填充到mainlistview即PullToRefreshView
         this.mainListView.setAdapter(listAdapter);
         
         //监听listview的点击操作
         this.mainListView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
 			public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
 				//在此进行单个job的展开，将weiboStatusList。get(position)传入
 				Log.i(TAG, "click item"+(position-1));
 				view.setBackgroundColor(Color.parseColor(getString(R.color.gray_light).toString()));
            	Intent it= new Intent(Accepted.this,ShowOneBB.class);
            	it.putExtra("bbjobid", (position-1));
            	it.putExtra("showType", Constants.CALL_FROM_ACCEPTER);
            	finishSelf();
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
        if(Accepted.this.flg_IsFilter == true)
        {/* 如果执行过过滤后loadmore，那么置标记让过滤的handler里再次过滤时也刷新而不再读取本地 */
        	Accepted.this.RefreshedWhenFilter = true;
        }
        this.RequestAcceptedJobFromSvr(this.page,this.reqJobStauts);
    }
    /**
     * 发送消息给服务器请求job信息
     * arg:job数量,job类型...
     */
    private void RequestAcceptedJobFromSvr(int page,JobStatus jobstauts)
    {	
    	Log.i(TAG, "req Accepted BB,page and jobstatus:"+page+jobstauts);
    	BaseUtil.innerComm.webappclient.ReqPickedBB(page*Constants.JOB_NUMBER_PER_PAGE,jobstauts,BaseUtil.getSelfUserInfo().getUserId());

    }
    //更新帮帮信息
    public void updateAcceptedJobList(List<JobDTO> JobList) {
    	int iLoop=0;
        //记录上次数量
        this.cnt_lst = Accepted.bangbangjoblist.size();
        if (this.flg_IsLoadMore == false){
            //如果是刷新就重置数据，并清空list
           this.cnt_lst = 1;
        }
        if(0 == JobList.size())
        {
        	bu.toastShow(this,"数据为空,请去别的地方逛逛吧");
        }
        Accepted.bangbangjoblist.clear();
        if(reqJobStauts.equals(JobStatus.ALL))
        {
        	Accepted.bangbangjoblist.addAll(JobList);//List更新，如何和listadapter联系起来更新？参考上面
        	Log.i(TAG, "add all"+Accepted.bangbangjoblist.size()+  JobList.size());
        }
        else
        {
        	Log.i(TAG, "add one");

        	while(iLoop<JobList.size())
        	{
        		if(this.reqJobStauts.equals(JobList.get(iLoop).getJobStatus()))
        		{
        			Accepted.bangbangjoblist.add(JobList.get(iLoop));//List更新，如何和listadapter联系起来更新？参考上面
        		}
        		iLoop++;
        	}
        }
        this.handler.sendEmptyMessageDelayed(UPDATE_UI,0);//更新
    }
	@Override
	protected void onResume(){
		Log.i(TAG, "onresume");
		super.onResume();
	}

	@Override
    public void onBackPressed() {
        this.finishSelf();
    }
	private void finishSelf()
	{
		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what)
		{
			case MessageId.REQUEST_ACCEPTED_SUCCESS_UI:
			{//不仅需要返回接受的job信息，还需要返回发布job的用户信息,两个表都要访问
				//而且在showonebb时还需要匹配带userinfo
				bangbangjoblistLocal = (List<JobDTO>) msg.obj;
				bu.checkHeadPhotoUpdated(this,bangbangjoblistLocal,SFtpRequest.getAvatarForOwner);

				updateAcceptedJobList(bangbangjoblistLocal);
				break;
			}
			case MessageId.REQUEST_ACCEPTED_FAIL_UI:
			{
				bu.toastShow(this,"更新失败，请稍后再试");
				break;
			}
		//	case MessageId.CONNECT_SERVER_ERROR:
		//	{
	//			Log.e(TAG, "connect server error");
	//			toastShow(this,"网络连接失败，请恢复后再试");

	//		}
	//		break;
			default:
				Log.e(TAG, "unexpected msg"+msg.what);
		}
	}
	/**
	 * 按键时监听
	*/
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

				Accepted.this.setJobStatusTv(view.getId());
			
		}
	}; 
	public void onRightMore()
	{
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.right_more_normal, null);
	    AlertDialog.Builder builder=  new AlertDialog.Builder(
	    		new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth));
	    
	    builder.setNegativeButton("取消", null)
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {	            	
            	filterBBjobWithJobStatus(choseJobStatus);
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
	    TextView tvRMSexName = (TextView)contentView.findViewById(R.id.right_more_sex_name);
	    tvRMSexName.setVisibility(View.GONE);
	    LinearLayout llSL1 = (LinearLayout)contentView.findViewById(R.id.right_more_seperateline1);
	    llSL1.setVisibility(View.GONE);
	    LinearLayout llRMSex = (LinearLayout)contentView.findViewById(R.id.right_more_sex);
	    llRMSex.setVisibility(View.GONE);
	    
	    tvJobAll = (TextView)contentView.findViewById(R.id.right_more_jobstatus_all);
	    tvJobAll.setOnClickListener(listener);
	    tvJobPub = (TextView)contentView.findViewById(R.id.right_more_jobstatus_pub);
	    tvJobPub.setOnClickListener(listener);
	    tvJobAcc = (TextView)contentView.findViewById(R.id.right_more_jobstatus_acc);
	    tvJobAcc.setOnClickListener(listener);
	    tvJobComp = (TextView)contentView.findViewById(R.id.right_more_jobstatus_comp);
	    tvJobComp.setOnClickListener(listener);
	    tvJobCls = (TextView)contentView.findViewById(R.id.right_more_jobstatus_cls);
	    tvJobCls.setOnClickListener(listener);
	    tvJobExp = (TextView)contentView.findViewById(R.id.right_more_jobstatus_exp);
	    tvJobExp.setOnClickListener(listener);
	    if(choseJobStatus.equals(JobStatus.ALL)){
	    	tvJobAll.setBackgroundResource(R.color.theme_blue);
	    }else if(choseJobStatus.equals(JobStatus.PUBLISHED)){
	    tvJobPub.setBackgroundResource(R.color.theme_blue);
	    }else if(choseJobStatus.equals(JobStatus.PICKED)){
	    	tvJobAcc.setBackgroundResource(R.color.theme_blue);
	    }else if(choseJobStatus.equals(JobStatus.COMPLETED)){
			tvJobComp.setBackgroundResource(R.color.theme_blue);
	    }else if(choseJobStatus.equals(JobStatus.CLOSED)){
			tvJobCls.setBackgroundResource(R.color.theme_blue);
	    }else if(choseJobStatus.equals(JobStatus.EXPIRED)){
			tvJobExp.setBackgroundResource(R.color.theme_blue);
	    }
	}
	
	private void setJobStatusTv(int rID)
	{
		tvJobAll.setBackgroundResource(R.drawable.background_right_more_chose);
		tvJobPub.setBackgroundResource(R.drawable.background_right_more_chose);
		tvJobAcc.setBackgroundResource(R.drawable.background_right_more_chose);
		tvJobComp.setBackgroundResource(R.drawable.background_right_more_chose);
		tvJobCls.setBackgroundResource(R.drawable.background_right_more_chose);
		tvJobExp.setBackgroundResource(R.drawable.background_right_more_chose);

		switch(rID)
		{
		case R.id.right_more_jobstatus_all:
			tvJobAll.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.ALL;
			break;
		case R.id.right_more_jobstatus_pub:
			tvJobPub.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.PUBLISHED;
			break;
		case R.id.right_more_jobstatus_acc:
			tvJobAcc.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.PICKED;
			break;
		case R.id.right_more_jobstatus_comp:
			tvJobComp.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.COMPLETED;
			break;
		case R.id.right_more_jobstatus_cls:
			tvJobCls.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.CLOSED;
			break;
		case R.id.right_more_jobstatus_exp:
			tvJobExp.setBackgroundResource(R.color.theme_blue);
			choseJobStatus = JobStatus.EXPIRED;
			break;
		default:
			break;
		}
		
	}
	private void filterBBjobWithJobStatus(JobStatus JobStatus)
	{
		reqJobStauts = JobStatus;
		if(reqJobStauts.equals(JobStatus.ALL))
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
			this.updateAcceptedJobList(bangbangjoblistLocal);
		}
		else
		{/*  如果在过滤且更新过的前提下再次过滤，则向服务器请求 */
			this.mainListView.startRefresh();
		}
		//	RequestAcceptedJobFromSvr();  //save server pressure, process this in client
		if(reqJobStauts.equals(JobStatus.ALL))
		{
			this.RefreshedWhenFilter=false;
		}
	}
}
