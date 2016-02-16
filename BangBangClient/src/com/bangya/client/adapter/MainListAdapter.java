package com.bangya.client.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.Util.Constants;
import com.bangya.client.Util.FileUtil;
import com.bangya.client.comm.PushReceiver;
import com.bangya.client.model.Job;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.JobStatus;
import com.joeapp.bangya.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 加载帮帮job的mainListAdapter
 * @author jiangzhi
 *
 */

public class MainListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
	private List<JobDTO> bangBangJobList;
	private String TAG="mainlistadpt";
	private DisplayImageOptions options;

    static class ListItemView{
    	public TextView PublisherName; //
       public ImageView imagephoto; 	//用户头像
       public TextView jobStatus;    //帮帮状态
        public TextView  deadTime;    //截止时间
        public TextView  title;			//帮帮标题
        public TextView distance;
        public ImageView ownerGender;
        public ImageView refresh_notify;
        public ImageView msg_nofity;
        public View vertical_seperate;


    }
	public MainListAdapter() {
		// TODO Auto-generated constructor stub
	}
    public MainListAdapter(Context context,List<JobDTO> bangBangJobList){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.bangBangJobList = bangBangJobList;
        

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
	public int getCount() {
		// TODO Auto-generated method stub
		return this.bangBangJobList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
        ListItemView listItemView = null;
        if (convertView == null){
            convertView = this.layoutInflater.inflate(R.layout.jobmainlist,null);
            listItemView = new ListItemView();
            listItemView.PublisherName = (TextView) convertView.findViewById(R.id.job_list_publisher_name);
            listItemView.deadTime = (TextView) convertView.findViewById(R.id.job_list_deadtime);
            listItemView.jobStatus = (TextView) convertView.findViewById(R.id.job_list_status);
            listItemView.imagephoto = (ImageView) convertView.findViewById(R.id.job_list_headimage);
            listItemView.title = (TextView) convertView.findViewById(R.id.job_list_title);
            listItemView.ownerGender = (ImageView) convertView.findViewById(R.id.job_list_gender);
            listItemView.refresh_notify = (ImageView) convertView.findViewById(R.id.job_list_refresh);
            listItemView.msg_nofity = (ImageView) convertView.findViewById(R.id.job_list_msg_notify);
            listItemView.vertical_seperate = (View)  convertView.findViewById(R.id.job_list_vertical_seperate);
            listItemView.distance = (TextView) convertView.findViewById(R.id.job_list_distance);
            convertView.setTag(listItemView);
        }else{
            listItemView = (ListItemView) convertView.getTag();
        }
        
        listItemView.PublisherName.setText(bangBangJobList.get(i).getOwnerNickName());
        listItemView.deadTime.setText(bangBangJobList.get(i).getDuetimeAsString());
        if(Constants.FEMALE.equals(bangBangJobList.get(i).getOwnerGender())){
        	listItemView.ownerGender.setImageResource(R.drawable.female);
        }
        else{
        	listItemView.ownerGender.setImageResource(R.drawable.male);
        }
    	System.out.println("display image for uid:"+bangBangJobList.get(i).getOwnerUid());
    	ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(bangBangJobList.get(i).getOwnerUid()), listItemView.imagephoto, options);

        setPushNotify(i,listItemView);
       // Log.i(TAG, "index of list: "+i+"jobstatus: "+bangBangJobList.get(i).getJobStatus());
        listItemView.jobStatus.setText((new Job()).getJobStatusStr(context, bangBangJobList.get(i).getJobStatus()));
      //  Log.i(TAG, "title: "+bangBangJobList.get(i).getTitle());
        listItemView.title.setText(bangBangJobList.get(i).getTitle());
        if(bangBangJobList.get(i).getOwnerUid() != BaseUtil.getSelfUserInfo().getUserId() 
        		&& bangBangJobList.get(i).getJobStatus() == JobStatus.PUBLISHED){
            listItemView.distance.setText(bangBangJobList.get(i).getDistanceForShow());
        }else{
        	listItemView.distance.setVisibility(View.INVISIBLE);
        }
		return convertView;
	}
	public void setPushNotify(int i,ListItemView listItemView){
        boolean isMsgActivited = false;
        boolean isStautsChanged = false;
        //handle job status changed or msg notify
       if(PushReceiver.PCCInfo.IsChanged()
    		/*(20150330 delete this) && bangBangJobList.get(i).getJobStatus()!= JobStatus.PUBLISHED */)
       {
    	   if(PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_OWNER)
    			   && bangBangJobList.get(i).getOwnerUid() == BaseUtil.getSelfUserInfo().getUserId()){ 
    		   //to owner and jobOwnerUid is app owner uid
    		   //to specify client,one job only have unique direction,so here only need jobid but direction
    		   if(PushReceiver.PCCInfo.isJobStatusRefreshed(bangBangJobList.get(i).getJobId())){
    			   isStautsChanged = true;
    		   }
    		   if(PushReceiver.PCCInfo.isMsgNotified(bangBangJobList.get(i).getJobId())){
    			   isMsgActivited = true;
    		   }
    	   }else if(PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_PICKER)
    			   && bangBangJobList.get(i).getPickerUid() == BaseUtil.getSelfUserInfo().getUserId()){
    		   //to picker and jobOwnerUid is not app owner uid
    		   if(PushReceiver.PCCInfo.isJobStatusRefreshed(bangBangJobList.get(i).getJobId())){
    			   isStautsChanged = true;
    		   }
    		   if(PushReceiver.PCCInfo.isMsgNotified(bangBangJobList.get(i).getJobId())){
    			   isMsgActivited = true;
    		   }
    	   }
       }
       if(isStautsChanged){
		   listItemView.refresh_notify.setImageResource(R.drawable.refresh);
		   
       }else{
    	   listItemView.refresh_notify.setVisibility(View.GONE);
       }
       if(isMsgActivited){
		   listItemView.msg_nofity.setImageResource(R.drawable.msg_notify);
       }else{
    	   listItemView.msg_nofity.setVisibility(View.GONE);
       }
       if(!isMsgActivited && !isStautsChanged){
    	   listItemView.vertical_seperate.setVisibility(View.GONE);
    	   
       }
	}
}
