package com.bangya.client.BBUI;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.TabListViewAdapter;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.model.SecondTabListViewItem;
import com.joeapp.bangya.R;

public class MyTrends extends Fragment implements InnerCommInterface{
	
	Button btMyPublished;
	Button btMyPicked;
	private String TAG = "MyBBTrends";
	//private boolean publicNotify = false;
	//private boolean pickNotify = false;
	static public View view;
	TabListViewAdapter myTrendsAdapter = null;
	public static boolean isPushRecived=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
    	view = inflater.inflate(R.layout.my_bangya_trends, container, false);
	//	BaseUtil.innerComm.addCurrentActivity(this, this.getActivity());

    	Log.i(TAG, "onCreateView");
    	updateMyTrends(view);
    	HomeActivity ha = new HomeActivity();
    	// this listener is for,
       ha.setOnRefreshListener(new HomeActivity.OnRefreshListener() {/* 更新监听 */
            @Override
            public void onRefresh() {
            	if(myTrendsAdapter !=null)
            		myTrendsAdapter.notifyDataSetChanged();
            	else
            		Log.e(TAG, "try to refresh MyTrends, but failed!");
            }
        });
       return view;
    }
    @Override

    public void onActivityCreated(Bundle savedInstanceState) { 
    	super.onActivityCreated(savedInstanceState);
    	//updateMyTrends(view);
    	Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onResume()
    {//if start another activity,and go back,onResume called ofter onStart
    	super.onResume();
    	
    	updateMyTrends(null);
    	Log.i(TAG, "onResume");
    }
    @Override
    public void onPause()
    {
    	super.onPause();
    	Log.i(TAG, "onPause");
    }
    @Override
    public void onStop()
    {
    	super.onStop();
    	Log.i(TAG, "onStop");
    }
    @Override
    public void onDestroyView() {
    	Log.i(TAG, "onDestroyView");
		//BaseUtil.innerComm.delCurrentActivity(this);
        super.onDestroyView();
    }
   // public void pushUpdate(){
    //	myTrendsAdapter.notifyDataSetChanged();
    	//用listern！！
   // }

    public void updateMyTrends(View view)
    {/*
    	publicNotify = false;
    	pickNotify = false;
    	if(PushReceiver.PCCInfo.IsChanged() && PushReceiver.PCCInfo.getChangdDetail().size() !=0){
    		if(PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_OWNER)){
    	      	publicNotify = true;
    	    }
    	   if(PushReceiver.PCCInfo.isDirectionEnable(Constants.PUSH_MSG_DIRECTION_TO_PICKER)){
    	   	   pickNotify = true;
    	   }
    	}

       Log.i(TAG, "updateMyTrends publicNotify: "+publicNotify+", pickNotify:"+pickNotify);
*/
        //生成动态数组，加入数据  
    //	Object icon[] ={R.drawable.ask_help,R.drawable.offer_help}; 
   // 	String title[]={getActivity().getString(R.string.published).toString(),getActivity().getString(R.string.accepted).toString()};
  /*      ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<2;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("icon", icon[i]);//图像资源的ID  
            map.put("title",	title[i]);  
            if((i == 0 && publicNotify == true)
               || (i == 1 && pickNotify == true)){
                map.put("notification", R.drawable.ui_notification_large);//图像资源的ID  
            }
          listItem.add(map);
        }  
   	 SimpleAdapter adapter = new SimpleAdapter(getActivity(),listItem,R.layout.bb_2nd_layer_list_view,
				new String[]{"icon","title","notification"},
				new int[]{R.id.snd_layer_list_icon,R.id.snd_layer_list_title,R.id.notification});
		*/
    	String title[]= {getActivity().getString(R.string.published).toString(),
    			getActivity().getString(R.string.accepted).toString()};
    	int icon[] ={R.drawable.ask_help,R.drawable.offer_help,}; 

		ListView lv = null;
		if(view != null)
		 lv = (ListView)view.findViewById(R.id.ListView01);
		else
		 lv = (ListView)getActivity().findViewById(R.id.ListView01);
		
		List<SecondTabListViewItem> listviewItem = new ArrayList<SecondTabListViewItem>();
		for(int i=0;i<2;i++){
			SecondTabListViewItem item = new SecondTabListViewItem();
			item.icon = icon[i];
			item.title = title[i];
			listviewItem.add(item);
		}
		 myTrendsAdapter = new TabListViewAdapter(getActivity(),TabListViewAdapter.fromMYTrends,
				 listviewItem);
		lv.setAdapter(myTrendsAdapter);
		   //添加点击  
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {  

            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,  
                    long arg3) {  
    			if(0 == position)
    			{
    				
    				Intent it= new Intent(getActivity(),Published.class);	
    				startActivity(it);
    			//	FindBB.this.onRightMore(view);
    			}else if(1 == position)
    			{
    				Intent it= new Intent(getActivity(),Accepted.class);	
    				startActivity(it);
    			}else
    			{
    				Log.e(TAG, "unkonw position");

    			}            
    		}  
        });  

    }
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
			case MessageId.PUSH_JOB_STATUS:
			case MessageId.PUSH_CHAT_MSG:
			{
        		Log.i(TAG, "recv push msgs,try to refresh MyTrends");

            	if(myTrendsAdapter !=null)
            		myTrendsAdapter.notifyDataSetChanged();
            	else
            		Log.e(TAG, "try to refresh MyTrends, but failed!");
            }
			break;
		default:
			Log.i(TAG, "unknown msg:"+msg.what);
			break;
		}

	}

}
