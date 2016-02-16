package com.bangya.client.BBUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bangya.client.adapter.TabListViewAdapter;
import com.bangya.client.model.SecondTabListViewItem;
import com.igexin.sdk.PushManager;
import com.joeapp.bangya.R;

public class AboutMe extends Fragment {
	private String TAG = "BBAboutMe";
	public static int settingPosition =2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layobtMyProfile for this fragment
    	View view = inflater.inflate(R.layout.bb_about_me, container, false);
        //生成动态数组，加入数据  
    	int icon[] ={R.drawable.profile,R.drawable.setting,
    			R.drawable.about,R.drawable.suggestion,R.drawable.logout};
    	// if setting position is changed, please change AboutMe.settingPosition
    	String title[]={getActivity().getString(R.string.profile).toString(),
    			getActivity().getString(R.string.setting).toString(),
    			getActivity().getString(R.string.about).toString(),
    			getActivity().getString(R.string.suggestion).toString(),
    			getActivity().getString(R.string.logout).toString()

    			};

		List<SecondTabListViewItem> listviewItem = new ArrayList<SecondTabListViewItem>();
		for(int i=0;i<5;i++){
			SecondTabListViewItem item = new SecondTabListViewItem();
			item.icon = icon[i];
			item.title = title[i];
			listviewItem.add(item);
		}
		ListView lv = (ListView)view.findViewById(R.id.bb_about_me_lv);

		TabListViewAdapter aboutMeAdapter = new TabListViewAdapter(getActivity(),TabListViewAdapter.fromAboutMe,
				 listviewItem);
		lv.setAdapter(aboutMeAdapter);
		
		   //添加点击  
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {  

            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,  
                    long arg3) {  
    			if(0 == position)
    			{
    				
    				Intent mIntent = new Intent(getActivity(),EditUserInfo.class);  
    			       Bundle mBundle = new Bundle();  
    			       mBundle.putSerializable("user",BaseUtil.getSelfUserInfo());  
    			       mIntent.putExtras(mBundle);
    			       startActivity(mIntent);  
    			}else if(1 == position)
    			{
    				Intent it= new Intent(getActivity(),Setting.class);	
    				startActivity(it);
    			}else if(4 == position)
    			{
    				new AlertDialog.Builder(getActivity())
    				.setTitle("确认退出当前账户?")
    				.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
    		            public void onClick(DialogInterface dialog, int which) {
    	    				BaseUtil bu = new BaseUtil();
    	    				bu.setSharePreferencesForToken(BaseUtil.getSelfUserInfo().getUserName(), "", getActivity());

    	    				HomeActivity.pushManagerInited = false;
    	    				Setting.isNotifyEnable = false;
    	    				PushManager.getInstance().stopService(getActivity());
    	    				Intent it= new Intent(getActivity(),HomeActivity.class);	
    	    		    	it.putExtra("IsLogin", false);
    	    				startActivity(it);
    	    				getActivity().finish();
    		            }
    				})
    				.setNegativeButton("取消", null)
    				.show();

    			}else if(2 == position)
    			{
    				Intent it= new Intent(getActivity(),About.class);	
    				startActivity(it);
    			}else if(3 == position)
    			{
    				Intent it= new Intent(getActivity(),Suggestion.class);	
    				startActivity(it);
    			}else
    			{
    				Log.e(TAG, "not hit any view");
    			}        
    		}  
        });  
        return view;
    }
	/**
	 * 按键时监听
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if(view.getId() == R.id.myProfile)
			{
				Intent it= new Intent(getActivity(),EditUserInfo.class);	
				startActivity(it);
			}else if(view.getId() == R.id.setting)
			{
			//	Intent it= new Intent(getActivity(),Accepted.class);	
			//	startActivity(it);
				 BaseUtil.toastShow(getActivity(),"暂未支持，非常抱歉");
			}else if(view.getId() == R.id.logOut)
			{
				SharedPreferences sp = getActivity().getSharedPreferences("BBSPName",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("username","");
				editor.putString("passwd", "");
				editor.commit();
				Intent it= new Intent(getActivity(),BBLogin.class);	
				startActivity(it);
				getActivity().finish();
			}else if(view.getId() == R.id.about)
			{
				Intent it= new Intent(getActivity(),About.class);	
				startActivity(it);
			}else if(view.getId() == R.id.suggestion)
			{
				Intent it= new Intent(getActivity(),Suggestion.class);	
				startActivity(it);
			}else
			{
				Log.e(TAG, "not hit any view");
			}
		}
	};*/
}
